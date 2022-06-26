// import React, {useEffect, useState} from 'react';
// import LottieView from 'lottie-react-native';
// import {
//   Text,
//   StatusBar,
//   View,
//   StyleSheet,
//   Platform,
//   TouchableOpacity,
//   Image,
// } from 'react-native';
// import {NativeModules, ImageBackground} from 'react-native';

// const {AtomSdkModule} = NativeModules;
// // const image = { uri: "./src/assets/images/img_mapdashboard.png" };
// const image = { uri: "https://reactjs.org/logo-og.png" };
// const App = () => {
//   useEffect(() => {
//     // const msg = await AtomSdkModule.onVPNStateChange();
//     AtomSdkModule.onVPNStateChange(msg => {
//       console.log('use effect', msg);
//     });
//   });

//   return (
//     // <View style={styles.container}>
//     //   {/* <LottieView source={require('./src/assets/animations/connecting_button_popup.json')} /> */}
//     //   <ImageBackground source={image} resizeMode="cover" style={styles.image}>
//     //     <StatusBar barStyle="dark-content" backgroundColor={'#e4e5ea'} />
//     //     {/* <Text style={styles.title}>Ivacy</Text> */}
//     //   </ImageBackground>
//     // </View>
//     <View style={styles.container}>
//     <ImageBackground source={image} resizeMode="cover" style={styles.image}>
//       <Text style={styles.text}>Inside</Text>
//     </ImageBackground>
//   </View>
//   );
// };

// const styles = StyleSheet.create({
//   // container: {
//   //   // backgroundImage: './src/assets/images/img_mapdashboard.png',
//   //   background: 'black',
//   //   flex: 1,
//   //   paddingTop: 50,
//   //   alignItems: 'center',
//   // },
//   image: {
//     flex: 1,
//     justifyContent: "center"
//   },
//   title: {
//     fontSize: 60,
//     color: '#fff',
//     marginVertical: 25,
//   },
//   iconsContainer: {
//     flexDirection: 'row',
//     alignItems: 'center',
//     justifyContent: 'space-evenly',
//     width: '100%',
//     paddingHorizontal: 50,
//   },
//   warningText: {
//     color: 'red',
//     fontWeight: 'bold',
//     letterSpacing: 1.5,
//     textAlign: 'center',
//   },
//   spacing: {
//     marginVertical: 10,
//   },
//   row: {
//     flexDirection: 'row',
//     justifyContent: 'space-between',
//     alignItems: 'center',
//     width: '40%',
//   },
//   icon: {
//     height: 40,
//     width: 40,
//     marginBottom: 15,
//   },
// });

// export default App;

import React, {useEffect, useRef, useState} from 'react';
import {
  ImageBackground,
  StyleSheet,
  Text,
  View,
  NativeModules,
  Button,
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

  useEffect(() => {
    DeviceEventEmitter.addListener('onConnected', event => {
      setAnimationUri(Connected);
      setConnected(true);
      console.log('event -->', event);
    });

    DeviceEventEmitter.addListener('onDisconnected', event => {
      setConnected(false);
      console.log('event -->', event);
    });

    DeviceEventEmitter.addListener('onStateChange', event => {
      console.log('event -->', event);
    });

    DeviceEventEmitter.addListener('onConnecting', event => {
      setAnimationUri(ConnectedLoop);
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
              loop={false}
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
