# Ask
Android runtime permissions make easy

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Ask-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3465)
# Why?

In marshmallow, android has introduced runtime permission check, that means when application runs, it asks user provide permission to run specific functionality. for example if you using Camera or saving files on external storage or location.

The android basic code to request permission is to complex and tedious to understand (if you don't trust me, check [here](http://developer.android.com/training/permissions/requesting.html) :smiley: ). **Ask** is a library make asking for the particular permission easy for developer. This is very simple and light weight library with just few lines of code and you good to go.

## Demo( How it looks!)

| Ask for permission     | Show rationale |
| ---      | ---       |
| ![show permission](https://github.com/00ec454/Ask/blob/master/asset/permission_1.png) | ![show rationale](https://github.com/00ec454/Ask/blob/master/asset/rationale.png)         |

## How to use.

* Very first step is to include this library in your project by adding following entry into your project's gradle dependencies

```groovy
dependencies {
	compile 'com.vistrav:ask:2.4'
}
```

* Adding the necessary permissions into your project manifest file
* Add the following code in your class to request the runtime permissions
```java
import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ask.on(this)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("Location permission need for map to work properly",
                        "In order to save file you will need to grant storage permission") //optional
                .go();
    }

    //optional
    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessGranted() {
        Log.i(TAG, "FILE  GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessDenied() {
        Log.i(TAG, "FILE  DENiED");
    }

    //optional
    @AskGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void mapAccessGranted() {
        Log.i(TAG, "MAP GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void mapAccessDenied() {
        Log.i(TAG, "MAP DENIED");
    }
}

```

* The setting rationale message is optional but it would be good in case user has declined the permission, there is chance for developer to explain app user why specific permission is needed

1. `forPermissions` method takes one or more permissions as argument
2. `withRationales` method takes one or more rationale message, usually it is good to provide same number of rationale messages as number of permissions

**IMPORTANT: If your application is running in any android verion lesser than Marshamallow, the all, requested permissions will be granted by default and you can find then in list provided by `granted` method**

##You can contribute!
In case you think you have some improvement, please feel free do pull request your feature and I would be happy to include it. Let's make this Ask very easy to use and rich with features.

##Other Userful Libraries
#### pop - a quick android dialog building lib
[![Github](https://img.shields.io/badge/github-pop-green.svg)](https://github.com/00ec454/pop) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Pop-green.svg?style=true)](https://android-arsenal.com/details/1/3400)

##License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
