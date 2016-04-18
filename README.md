# Ask
Android runtime permissions make easy

## How to use.

Add the following code in your class and that is all you need. You can do all you want in `granted` or `denied` methods

```java
        Ask.on(this)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) //one or more permissions
                .withRationales("Location permission need for map to work properly", 
                        "In order to save file you will need to grant storage permission") //optional
                .when(new Ask.Permission() {
                    @Override
                    public void granted(List<String> permissions) {
                        Log.i(TAG, "granted :: " + permissions);
                    }

                    @Override
                    public void denied(List<String> permissions) {
                        Log.i(TAG, "denied :: " + permissions);
                    }
                }).go();

```

## How to include it in your project:

```groovy
dependencies {
	compile 'com.vistrav:ask:1.0'
}

##You can contribute!
In case you think you have some improvement, please feel free do pull request your feature and I would be happy to include it. Let's make this Ask very easy to use and rich with features.

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
