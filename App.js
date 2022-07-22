import React, {useEffect, useRef, useState} from 'react';
import {
  ImageBackground,
  StyleSheet,
  Text,
  View,
  NativeModules,
  TouchableWithoutFeedback,
  NativeEventEmitter,
  AsyncStorage,
} from 'react-native';
import LottieView from 'lottie-react-native';

//Animations
import ConnectingPopup from './src/assets/animations/connecting_button_popup.json';
import ConnectingIn from './src/assets/animations/connecting_inn.json';
import Connected from './src/assets/animations/connected_pulse.json';
import ConnectedLoop from './src/assets/animations/connecting_loop.json';

const {AtomSdkModule} = NativeModules;
const DeviceEventEmitter = new NativeEventEmitter();

const App = () => {
  //Animation ref
  const animation = useRef(null);

  //component states
  const [aniamtionUri, setAnimationUri] = useState(ConnectingPopup);
  const [isConnected, setConnected] = useState(false);
  const [isLoading, setLoading] = useState(false);
  const [connectionDetails, setConnectionDetails] = useState(false);

  useEffect(() => {
    DeviceEventEmitter.addListener('onConnected', event => {
      setAnimationUri(Connected);
      setLoading(false);
      setConnected(true);

      saveData('connectionDetails', JSON.stringify(event.data));
      setConnectionDetails(JSON.parse(event.data).connectionDetails);
    });

    DeviceEventEmitter.addListener('onDisconnected', event => {
      setConnected(false);
      setLoading(false);
      // console.log('event onDisconnected -->', event);
    });

    DeviceEventEmitter.addListener('onStateChange', event => {
      // console.log('event onStateChange  -->', event);
    });

    DeviceEventEmitter.addListener('onUnableToAccessInternet', event => {
      // console.log('event onUnableToAccessInternet  -->', event);
    });

    DeviceEventEmitter.addListener('onConnecting', event => {
      setAnimationUri(ConnectedLoop);
      setLoading(true);
      // console.log('event onConnecting -->', event);
    });

    //Atom Sdk Initialization
    AtomSdkModule.atomInitialize(isAtomInitialized => {
      // console.log('use effect atomInitialize', isAtomInitialized);
    });
  });

  useEffect(() => {
    // console.log(isConnected, connectionDetails);

    readData('connectionDetails', value => {
      let data = JSON.parse(value);
      setConnectionDetails(JSON.parse(data)?.connectionDetails);
    }),
      //Get Current Status of VPN
      AtomSdkModule.getCurrentVpnStatus(currentStatus => {
        // console.log('currentStatus', currentStatus);
        if (currentStatus == 'CONNECTED') {
          setConnected(true);
          setAnimationUri(Connected);
          // console.log(" AsyncStorage.getItem('connectionDetails')",  AsyncStorage.getItem('connectionDetails'))
        } else {
          setConnected(false);
        }
      });
  }, []);

  const onResume = () => {};
  const onPause = () => {};
  const onPress = () => {
    if (!isConnected) {
      setLoading(true);
      setAnimationUri(ConnectingIn);

      AtomSdkModule.connectVPN(state => {
        // console.log('state', state);
      });
    } else {
      AtomSdkModule.disconnectVPN();
      setAnimationUri(ConnectingPopup);
      // console.log('disconeect call');
    }
  };
  const saveData = async (key, value) => {
    try {
      await AsyncStorage.setItem(key, value);
      // alert('Data successfully saved');
    } catch (e) {
      // alert('Failed to save the data to the storage');
    }
  };

  const readData = async (key, callback) => {
    try {
      const value = await AsyncStorage.getItem(key);
      if (value) {
        callback(value);
      }
    } catch (e) {
      alert('Failed to fetch the input from storage', e);
    }
  };

  return (
    <View style={styles.container}>
      <ImageBackground
        source={require('./src/assets/images/img_mapdashboard.png')}
        resizeMode="cover"
        style={styles.image}>
        <View stlye={styles.content}>
          <View style={styles.headingView}>
            <Text style={styles.mainHeading}>
              Secure Connection {isConnected ? 'Enabled' : 'Disabled'}
            </Text>

            <View>
              <Text style={styles.subHeading}>
                {isConnected ? `Your Location` : 'Tap to Connect'}{' '}
              </Text>
              {isConnected && (
                <Text style={styles.subHeading}>
                  {connectionDetails.country}
                </Text>
              )}
            </View>
          </View>
          <TouchableWithoutFeedback onPress={onPress}>
            {aniamtionUri ? (
              <LottieView
                style={styles.lottie}
                autoPlay
                loop={(isLoading || isConnected) && true}
                ref={animation}
                source={aniamtionUri}
                resume={onResume}
                pause={onPause}
              />
            ) : null}
          </TouchableWithoutFeedback>
        </View>
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
  headingView: {},
  mainHeading: {
    color: '#DAA520',
    textAlign: 'center',
    fontSize: 25,
    paddingBottom: 10,
  },
  subHeading: {
    color: 'white',
    textAlign: 'center',
    fontSize: 15,
  },
  lottie: {
    width: '100%',
  },
  content: {
    flex: 1,
    width: 90,
    borderWidth: 'thick',
    borderColor: '#f0f',
  },
});

export default App;
