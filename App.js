import React, {useEffect, useRef, useState} from 'react';
import {
  ImageBackground,
  StyleSheet,
  Text,
  View,
  NativeModules,
  TouchableWithoutFeedback,
  DeviceEventEmitter,
} from 'react-native';
import LottieView from 'lottie-react-native';

//Animations
import ConnectingPopup from './src/assets/animations/connecting_button_popup.json';
import ConnectingIn from './src/assets/animations/connecting_inn.json';
import Connected from './src/assets/animations/connected_pulse.json';
import ConnectedLoop from './src/assets/animations/connecting_loop.json';

const {AtomSdkModule} = NativeModules;

const App = () => {
  //Animation ref
  const animation = useRef(null);

  //component states
  const [aniamtionUri, setAnimationUri] = useState(ConnectingPopup);
  const [isConnected, setConnected] = useState(false);
  const [isLoading, setLoading] = useState(false);


  useEffect(() => {
    DeviceEventEmitter.addListener('onConnected', event => {
      setAnimationUri(Connected);
      setLoading(false);
      setConnected(true);

      console.log('event -->', event);
    });

    DeviceEventEmitter.addListener('onDisconnected', event => {
      setConnected(false);
      setLoading(false);
      console.log('event -->', event);
    });

    DeviceEventEmitter.addListener('onStateChange', event => {
      console.log('event -->', event);
    });

    DeviceEventEmitter.addListener('onConnecting', event => {
      setAnimationUri(ConnectedLoop);
      setLoading(true);
      console.log('event -->', event);
    });

    AtomSdkModule.atomInitialize(isAtomInitialized => {
      console.log('use effect atomInitialize', isAtomInitialized);
    });
    
  }, []);

  const onResume = () => {};
  const onPause = () => {};
  const onPress = () => {
    if (!isConnected) {
      setLoading(true);
      setAnimationUri(ConnectingIn);

      AtomSdkModule.connectVPN(state => {
        console.log('state', state);
      });
    } else {
      AtomSdkModule.disconnectVPN();
      setAnimationUri(ConnectingPopup);
      console.log("disconeect call")
    }
  };


  console.log("aniamtionUri", aniamtionUri)

  return (
    <View style={styles.container}>
      <ImageBackground
        source={require('./src/assets/images/img_mapdashboard.png')}
        resizeMode="cover"
        style={styles.image}>
        <Text style={styles.mainHeading}>Secure Connection</Text>
        <TouchableWithoutFeedback onPress={onPress}>
          {aniamtionUri ? (
            <LottieView
              autoPlay
              loop={(isLoading || isConnected )&& true}
              ref={animation}
              source={aniamtionUri}
              resume={onResume}
              pause={onPause}
            />
          ) : null}
        </TouchableWithoutFeedback>
      </ImageBackground>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  image: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#242461',
  },
  mainHeading: {
    color: 'white',
    fontSize: 30,
    lineHeight: 84,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 400,
  },
});

export default App;
