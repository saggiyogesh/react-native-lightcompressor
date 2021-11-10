import * as React from 'react';
import {
  StyleSheet,
  View,
  Button,
  PermissionsAndroid,
  Alert,
} from 'react-native';
import { compress, VideoQuality } from 'react-native-lightcompressor';

const requestCameraPermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
      {
        title: 'Cool Photo App Camera Permission',
        message:
          'Cool Photo App needs access to your camera ' +
          'so you can take awesome pictures.',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('You can use the camera');
    } else {
      console.log('Camera permission denied');
    }
  } catch (err) {
    console.warn(err);
  }
};

export default function App() {
  const uri = 'file:///storage/emulated/0/DCIM/Camera/VID20211101150348.mp4';

  return (
    <View style={styles.container}>
      <Button title="request permissions" onPress={requestCameraPermission} />
      <Button
        title="hello"
        onPress={async () => {
          console.log('start--');
          const r = await compress({
            uri,
            quality: VideoQuality.MEDIUM,
            onProgress: (p: number) => console.log('progress--> ', p),
            onStart: () => console.log('start'),
          });

          console.log('response--', r);
          Alert.alert('done');
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
