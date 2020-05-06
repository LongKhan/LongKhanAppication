const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationToFCMToken = functions.firestore.document('notification/{mUid}').onWrite(async (event) => {
    var target = event.after.get('target');
    var txt = event.after.get('txt');

    var message = {
        notification: {
            body: txt,
        },
        token: target,
    }

    let response = await admin.messaging().send(message);
    console.log(response);
});