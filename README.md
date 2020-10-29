# nebesaTv7Android

[TV7](https://www.nebesatv7.com/) Android smart TV application. This application locale is __ru__ (Russian).

Developer pages of [Android TV](https://developer.android.com/tv).
Guide of [Android TV](https://developer.android.com/training/tv).

## Instructions

### Download and install git
  - If your computer OS is windows you can download the __git__ from [here](https://git-scm.com/download/win).
  - If your computer OS is linux (Ubuntu) you can install __git__ using package manager.

### Clone repository
  - Clone this repository to your computer disk.
    - __git clone https://github.com/heaven-dev/nebesaTv7Android.git__

### Download and install Android Studio
  - You can download Android Studio from [here](https://developer.android.com/studio).
  - When the download is complete start the setup program.
    - If asked during the setup you have to install also an emulator.
    - If asked during the setup you have to select __Android 10.0 (Q) platform__.

### Open project to Android Studio
  - When the setup is complete start the Android Studio.
  - Select __Open existing Android Studio project__ to open the project.

### Install emulator
  - You can install the emulator and an other tools from here: __Tools -> SDK Manager -> Appearance & Behaviour -> System settings --> Android SDK__
    - Select __SDK Tools__ tab.
      - Select the __Android SDK Build-Tools__ if needed.
      - Select the __Android Emulator__ if needed.
      - Select the __Android SDK Platform-Tools__ if needed.
      - Select the __Google Web Driver__ if needed (probably this is needed to run the application on a real device).
      - Select __Apply__ and __OK__.

### Create virtual device
  - Select __Tools -> AVD Manager__.
    - Select __+ Create virtual device__ from the left bottom of the modal.
    - Select __TV Category__.
    - Select __Android TV (720p)__.
      - Later you can try also other resolutions.
    - Select __Next__.
    - You can try API levels from __23 to 28__, but you can select only one at this time.
    - Select __Marsmallow__ system image at first.
    - Select __Next__ and it downloads the system image if not already downloaded.
    - You can give a name to the __AVD__.
    - Select __Automatic__ from the __Graphics__ dropdown.
    - Select __Landscape__
    - Select __Finish__.
    - Your virtual device is added to the list of __Your Virtual Devices__.

### Start Virtual Device
  - Select __Tools -> AVD Manager__ if needed.
  - Start virtual device from the __green triangle__ of the __Actions__ menu.
  - You can open the menu from the __grey triangle__ of the __Actions__ menu.
    - Sometimes you need to __Wipe data__ from the virtual device if everything isn't work like expected.

### Launch application to the Virtual Device
  - Select __Run --> Run app__ to launch a release version of the application.
  - Select __Run --> Debug app__ to debug the application.
  - Select __Run --> Profile app__ to profile the application.

### Remote control of TV Emulator
  - I didn't find nice remote control for the TV emulator, but you can use simple keypad.
    - Click __three dots__ from the toolbar on the right side of emulator.
    - __Extended controls__ modal opens.
    - Select __Directional pad__ from the left side menu of the modal and you can simulate some of the remote control buttons.
    - Back button is the __triangle__ on toolbar above the __three dots__ mentioned above.

### Run application on real device
  - Follow instructions from [here](https://developer.android.com/training/tv/start/start#run-on-a-real-device).
  - [This](https://developer.android.com/studio/run/device) probably helps.

### Build project
  - Follow instructions from [here](https://developer.android.com/studio/run#reference).

### Minimum Android version to run this application
  - Marshmallow
    - API level 23
    - Android 6.0 (Android TV)

### License
 - [MIT](https://github.com/heaven-dev/nebesaTv7Android/blob/master/LICENSE.md)
