import sys
import tensorflow as tf
data =[]
for i in range(2,7):
    data.append(int(sys.argv[i]))


if sys.argv[1] == '1':
    reg1 = tf.keras.models.load_model('G:\\distributedscheduling-master\\NeuralNetworkConti\\NNCONTIMksCloud.model')
    pred1 = reg1.predict([[data]])
    print(pred1.argmax())

if sys.argv[1] == '2':
    reg2 = tf.keras.models.load_model('G:\\distributedscheduling-master\\NeuralNetworkConti\\NNCONTIThroughPutCloud.model')
    pred2 = reg2.predict([[data]])
    print(pred2.argmax())

if sys.argv[1] == '3':
    reg3 = tf.keras.models.load_model('G:\\distributedscheduling-master\\NeuralNetworkConti\\NNCONTICostCloud.model')
    pred3 = reg3.predict([[data]])
    print(pred3.argmax())

if sys.argv[1] == '4':
    reg4 = tf.keras.models.load_model('G:\\distributedscheduling-master\\NeuralNetworkConti\\NNCONTIDICloud.model')
    pred4 = reg4.predict([[data]])
    print(pred4.argmax())



