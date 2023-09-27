/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import type {PropsWithChildren} from 'react';
import { NativeModules } from "react-native"
const { CustomMethods } = NativeModules
import { Button } from 'react-native'

import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

type SectionProps = PropsWithChildren<{
  title: string;
}>;

function Section({children, title}: SectionProps): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}>
        {title}
      </Text>
      <Text
        style={[
          styles.sectionDescription,
          {
            color: isDarkMode ? Colors.light : Colors.dark,
          },
        ]}>
        {children}
      </Text>
    </View>
  );
}

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter
    
    
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
        
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={{backgroundColor:backgroundStyle.backgroundColor,height:'100%'}}>
        {/* <Header /> */}
        <View
          style={{
            backgroundColor:  backgroundStyle.backgroundColor,
            marginTop:'50%',flex:1
          }}>
            {/* <Button
      onPress={() => nativeSimpleMethod() }
      title="Simple Method"
    />
    <Button
      onPress={() => nativeSimpleMethodReturns() }
      title="Simple Method Returns"
    /> */}
    <Button
      onPress={() => initiatePaymentWith() }
      title="Initiate Payment"
    />
    {/* <Button
      onPress={() => nativeResolvePromise() }
      title="Reject Promise"
    />
    <Button
      onPress={() => nativeRejectPromise() }
      title="Reject Promise"
    /> */}
   
  
          {/* <LearnMoreLinks /> */}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});


const initiatePaymentWith = () => {
  CustomMethods.initiatePaymentWith(
    'example',
    result => {
      console.log(result);
     
      //alert(result)
    }
  )
}



export default App;
