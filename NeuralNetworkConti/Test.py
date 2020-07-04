import tensorflow as tf
data =[689,22,35334575,2692,12967]
reg1 = tf.keras.models.load_model('G:\\distributedscheduling-master\\NeuralNetworkConti\\NNCONTIDICloud.model')
pred = reg1.predict([[data]])
print(pred.argmax())
