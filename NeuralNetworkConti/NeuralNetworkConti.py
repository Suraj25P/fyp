import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn import preprocessing
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix
from sklearn.metrics import accuracy_score
from sklearn.metrics import classification_report
import matplotlib.pyplot as plt
import seaborn as sns
dataset = pd.read_csv('BEstDIConti.csv')
X = preprocessing.scale(dataset.iloc[:, :5].values)
y = dataset['ALGO']
print(X)
print(y)
y = tf.keras.utils.to_categorical(dataset['ALGO'])
print(y)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.1)
model = tf.keras.models.Sequential()
model.add(tf.keras.layers.Dense(6, input_dim=5, activation='relu'))
model.add(tf.keras.layers.Dense(6, activation='relu'))
model.add(tf.keras.layers.Dense(6, activation='relu'))
model.add(tf.keras.layers.Dense(6, activation='softmax'))

model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])
history = model.fit(X_train, y_train, validation_data=(X_test, y_test), epochs=5000,batch_size=1000)
y_pred = model.predict(X_test)
pred = list()
for i in range(len(y_pred)):
    pred.append(np.argmax(y_pred[i]))
test = list()
for i in range(len(y_test)):
    test.append(np.argmax(y_test[i]))

a = accuracy_score(pred,test)

cm =confusion_matrix(test,pred)
print(cm)
print(classification_report(test, pred))
print('Accuracy is:', a*100)
#model.save('NNCONTIThroughPutCloud.model')
plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('Model loss')
plt.ylabel('Loss')
plt.xlabel('Epoch')
plt.legend(['Train', 'Test'], loc='upper left')
plt.show()
