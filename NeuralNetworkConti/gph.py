import pandas as pd
import seaborn as sns
import  matplotlib.pyplot as plt
dataset1 = pd.read_csv("BEstMksConti.csv")
print(dataset1)
train_dataset = dataset1.sample(frac=0.8,random_state=0)
test_dataset =dataset1.drop(train_dataset.index)
print(train_dataset)
print(test_dataset)
sns.pairplot(train_dataset[['NOOFTASKS','NOOFMAC','LENGTH','LOAD','ARRIVAL']] , diag_kind='kde')
sns.pairplot(train_dataset[['NOOFTASKS','NOOFMAC','LENGTH','LOAD','ARRIVAL','ALGO']],hue='ALGO')
plt.show()