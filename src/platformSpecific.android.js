import React, {Component} from 'react';
import {
  AppRegistry
} from 'react-native';

import Navigation from './Navigation';
import utils from './utils';

import {
  RctActivity
} from 'react-native-navigation';

var resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');

function startSingleScreenApp(params) {
  let screen = params.screen;
  if (!screen.screen) {
    console.error('startSingleScreenApp(params): screen must include a screen property');
    return;
  }

  addNavigatorParams(screen);
  addNavigatorButtons(screen);
  addNavigationStyleParams(screen);
  RctActivity.startSingleScreenApp(screen);
}

function startTabBasedApp(params) {
  if (!params.tabs) {
    console.error('startTabBasedApp(params): params.tabs is required');
    return;
  }

  params.tabs.forEach(function (tab, idx) {
    addNavigatorParams(tab, null, idx);
    addNavigatorButtons(tab);
    addNavigationStyleParams(tab);
  });

  RctActivity.startTabBasedApp(params.tabs);
}

function navigatorPush(navigator, params) {
  addNavigatorParams(params, navigator);
  addNavigatorButtons(params);
  addNavigationStyleParams(params);
  RctActivity.navigatorPush(params);
}

function navigatorPop(navigator, params) {
  RctActivity.navigatorPop(navigator);
}

function showModal(params) {
  addNavigatorParams(params);
  addNavigatorButtons(params);
  addNavigationStyleParams(params);
  RctActivity.showModal(params);
}

function dismissModal() {
  RctActivity.dismissModal();
}

function dismissAllModals(params) {
  RctActivity.dismissAllModals(params.animationType);
}

function addNavigatorParams(screen, navigator = null, idx = '') {
  screen.navigatorID = navigator ? navigator.navigatorID : utils.getRandomId() + '_nav' + idx;
  screen.screenInstanceID = utils.getRandomId();
  screen.navigatorEventID = navigator ? navigator.navigatorEventID : screen.screenInstanceID + '_events';
}

function addNavigatorButtons(screen) {
  const Screen = Navigation.getRegisteredScreen(screen.screen);
  Object.assign(screen, Screen.navigatorButtons);

  // Get image uri from image id
  if (screen.rightButtons) {
    screen.rightButtons.forEach(function(button) {
      if (button.icon) {
        const icon = resolveAssetSource(button.icon);
        if (icon) {
          button.icon = icon.uri;
        }
      }
    });
  }
}

function addNavigationStyleParams(screen) {
  const Screen = Navigation.getRegisteredScreen(screen.screen);
  screen.navigatorStyle = Screen.navigatorStyle;
}

function navigatorSwitchToTab(navigator, params) {
    RctActivity.switchTabInPager(params.tabIndex);
}

function lockToPortrait() {
  return RctActivity.lockToPortrait();
}

function lockToLandscape() {
  return RctActivity.lockToLandscape();
}

function lockToSensorLandscape() {
  return RctActivity.lockToSensorLandscape();
}

function unlockAllOrientations() {
  return RctActivity.unlockAllOrientations();
}

function showMaterialDialog(options: any){
  var callbackNames = [
    'onPositive',
    'onNegative',
    'onNeutral',
    'onAny',
    'itemsCallback',
    'itemsCallbackSingleChoice',
    'itemsCallbackMultiChoice',
    'showListener',
    'cancelListener',
    'dismissListener',
  ];

  var finalOptions = Object.assign({}, options);

  var callbacks = {
    error: (err, op) => console.error(err, op),
  }

  // Remove callbacks from the options, and store them separately
  callbackNames.forEach(cb => {
    if (cb in finalOptions) {
      callbacks[cb] = finalOptions[cb];
      finalOptions[cb] = true;
    }
  });

  // Handle special case of input separately
  if ('input' in finalOptions) {
    finalOptions.input = Object.assign({}, finalOptions.input);
    var inputCallback = finalOptions.input.callback || (x => console.log(x));
    finalOptions.input.callback = true;
    callbacks['input'] = inputCallback;
  }

  // Parse the result form multiple choice dialog
  if ('itemsCallbackMultiChoice' in callbacks) {
    var originalCallback = callbacks.itemsCallbackMultiChoice;
    callbacks.itemsCallbackMultiChoice = selected => {
      var indices = selected.split(',').map(x => parseInt(x));
      var elements = indices.map(ind => (finalOptions.items || [])[ind]);

      originalCallback(indices, elements);
    }
  }

  var callbackFunc = (cb, ...rest) => callbacks[cb](...rest);

  return RctActivity.showMaterialDialog(finalOptions, callbackFunc);
}

export default {
  startTabBasedApp,
  startSingleScreenApp,
  navigatorPush,
  navigatorPop,
  showModal,
  dismissModal,
  dismissAllModals,
  navigatorSwitchToTab,
  lockToPortrait,
  lockToLandscape,
  lockToSensorLandscape,
  unlockAllOrientations,
  showMaterialDialog
}
