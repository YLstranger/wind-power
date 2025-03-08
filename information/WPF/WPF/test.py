import pandas as pd
data = pd.read_excel('D:\code_python\WPF\data\Wind farm site 1 (Nominal capacity-99MW)\wp-1-2019-01-01.xlsx')
print(data.iloc[:,0].dtype)