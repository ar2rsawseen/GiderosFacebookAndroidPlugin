<h1>Gideros Facebook plugin for Android</h1>

Installation instructions:
<ul>
<li>Download Facebook SDK here: http://developers.facebook.com/android/</li>
<li>Facebook SDK installation instructions here: http://developers.facebook.com/android/
<ul>
<li>Download and unzip</li>
<li>Import SDK into eclipse <strong>(Note you need to import it in workspace and check copy all files)</strong></li>
</ul>
</li>
<li>Export Gideros project to Android project</li>
<li>Import Android project to eclipse <strong>(Note you need to import it in workspace and check copy all files or Facebook SDK won't work)</strong></li>
<li>Link Facebook SDK to your project:
<ul>
<li>Right click on your project</li>
<li>Select properties</li>
<li>Choose Android</li>
<li>In library section click Add and choose FacebookSDK</li>
<li>If it's not there or not linking properly, it means that you haven't imported FacebookSDK into eclipse or it's not in your workspace or your project is not in your workspace</li>
</ul>
</li>
<li>Copy contents of FacebookPlugin.zip into your project:
<ul>
<li>Copy .so files to each separate armeabi folder</li>
<li>Copy facebook folder to src/giderosmobile/android/plugins folder</li>
</ul>
<li>Inside your main activity:
<ul>
<li>Add lib as System.loadLibrary("facebook");</li>
<li>Add external class as "com.giderosmobile.android.plugins.facebook.GFacebook"</li>
<li>Add this line inside onActivityResult method: GFacebook.fb.authorizeCallback(requestCode, resultCode, data); (This step will not be needed, when plugin will be included in Gideros SDK)</li>
</ul>
</li>
</li>
<li>Clean and launch the app, if it does not work, come and complain here, because I may have also screwed something up :) </li>
</ul>
