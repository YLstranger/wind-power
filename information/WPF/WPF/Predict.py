import pandas as pd
import numpy as np
from pathlib import Path
import argparse
import logging
from onnxruntime import InferenceSession
from datetime import datetime, timedelta

RED = "\033[31m"
RESET = "\033[0m"

parser = argparse.ArgumentParser()
parser.add_argument("--m_name", type=str, default="MSCGCN-xLSTM")
parser.add_argument("--step", type=int, default=48)#6,12,48,60=>1.5,3,12,15
parser.add_argument("--index", type=int, default=1)
parser.add_argument(
    "--f_path",
    type=str,
    default=r"D:\code_python\WPF\data\Wind farm site 1 (Nominal capacity-99MW)\wp-1-2019-01-01.xlsx",
)

args = parser.parse_args()


def data_preprocess(data_path: Path):
    if not data_path.exists():
        logging.error(f"{RED}Data file {data_path.as_posix()} not found{RESET}")
        raise FileNotFoundError(
            f"{RED}Data file {data_path.as_posix()} not found{RESET}"
        )

    # 读取数据并将第一列解析为日期时间格式
    df = pd.read_excel(data_path, parse_dates=[0])

    # 忽略时间列，选择数值数据
    data = df.iloc[:96, 1:].to_numpy().astype(np.float32)
    std_, mean_ = data.std(axis=0, keepdims=True) + 1e-7, data.mean(
        axis=0, keepdims=True
    )
    data = (data - mean_) / std_
    endtime = df.iloc[95, 0]

    def reverse_f(data: np.ndarray) -> np.ndarray:
        # 利用保存预处理的信息将模型输出映射回去
        return data * (std_[0, -1] + 1e-7) + mean_[0, -1]

    # 返回处理后的数据、时间信息、反向映射函数
    del df
    return data[None, :, :], endtime, reverse_f


def predict(m_name: str, index: int | str, f_path: str,step:int=48):
    base_path = Path(__file__).parent.absolute()
    print("base_path", base_path)
    f_path = Path(f_path).absolute()
    print("f_parh", f_path)
    model_path = base_path / "onnx_models" / f"{m_name}-{index}-{step}.onnx"
    print("model_path", model_path)
    if not model_path.exists():
        logging.error(
            f"{RED}Model {m_name}-{index}-{step}.onnx not found in {model_path.as_posix()}{RESET}"
        )
        raise FileNotFoundError(
            f"{RED}Model {m_name}-{index}-{step}.onnx not found in {model_path.as_posix()}{RESET}"
        )

    

    sess = InferenceSession(model_path.as_posix())
    input_name = sess.get_inputs()[0].name
    output_name = sess.get_outputs()[0].name

    data, endtime, reverse_f = data_preprocess(f_path)
    out = (
        reverse_f(sess.run([output_name], {input_name: data})[0])
        .reshape(1, -1)
        .astype(object)
    )

    start = pd.to_datetime(endtime)
    interval = timedelta(minutes=15)
    atimes = np.array(
        [start + i * interval for i in range(1, out.shape[1] + 1)], dtype=object
    ).reshape(1, -1)
    out = np.concatenate([atimes, out], axis=0)
    out = pd.DataFrame(out.T, columns=["DATETIME", "Power"])
    # 将 DATETIME 列转换为字符串格式
    # out.loc[:, 'DATETIME'] = out.loc[:, 'DATETIME'].dt.strftime('%Y-%m-%d %H:%M:%S')
    # out.loc[:, 'DATETIME'] = out.loc[:, 'DATETIME'].astype(str)
    out['DATETIME'] = out['DATETIME'].apply(lambda x: x.strftime('%Y-%m-%d %H:%M:%S'))
    print(out.loc[:,'DATETIME'].dtype)
    # out.loc[:, 'DATETIME'] = pd.to_datetime(out.loc[:, 'DATETIME'])
    out.loc[:, "Power"] = out.loc[:, "Power"].astype("float32")
    # 在 base_path 路径下创建 Out 文件夹，并设置为输出路径
    out_folder = base_path / "out"
    out_folder.mkdir(exist_ok=True)  # 创建 Out 文件夹（如果不存在）

    # 生成输出文件路径
    out_path = out_folder / f"{f_path.stem}_pred.xlsx"

    out.to_excel(out_path, index=False)


if __name__ == "__main__":
    predict(args.m_name, args.index, args.f_path, args.step)