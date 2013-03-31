package com.giderosmobile.android.plugins.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.Facebook.ServiceListener;
import com.facebook.android.FacebookError;
import com.giderosmobile.android.player.GiderosApplication;

@SuppressWarnings("deprecation")
public class GFacebook
{
	private static WeakReference<Activity> sActivity;
	public static long sData;
	public static Facebook fb;
	private static AsyncFacebookRunner fbr;
	
	public static void onCreate(Activity activity)
	{
		sActivity =  new WeakReference<Activity>(activity);
		Log.d("GFacebook", "created");
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d("GFacebook", "activity result from class");
		//GFacebook.fb.authorizeCallback(requestCode, resultCode, data);
	}
	
	static public void init(long data)
	{
		sData = data;
	}
	
	static public void cleanup()
	{
    	if (fb != null)
    	{
    		sData = 0;
    		fb = null;
    		fbr = null;
    	}
	}

	private static void setAppId(String appId){
		Log.d("GFacebook", "appId: " + appId);
		fb = new Facebook(appId);
		Log.d("GFacebook", "appId2: " + fb.getAppId());
		String token = getFBToken();
		if(token != "")
		{
			Log.d("GFacebook", "restoring token: " + getFBToken());
			fb.setAccessToken(getFBToken());
			fb.setAccessExpires(getFBTokenExpires());
		}
		fbr = new AsyncFacebookRunner(fb);
	}
	
	private static void authorize(){
		 fb.authorize(sActivity.get(), new GFacebookAuth());
	}
	
	private static void authorize(Object[] permissions){
		 fb.authorize(sActivity.get(), (String[]) permissions, new GFacebookAuth());
	}
	
	private static void logout(){
		fbr.logout(sActivity.get(), new GFacebookLogout());
	}
	
	private static boolean isSessionValid(){
		return fb.isSessionValid();
	}
	
	private static void dialog(final String action){
		sActivity.get().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fb.dialog(sActivity.get(), action, new GFacebookDialog());
			}
		});
	}
	
	private static void dialog(final String action, final Object parameters){
		sActivity.get().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fb.dialog(sActivity.get(), action, (Bundle)parameters, new GFacebookDialog());
			}
		});
	}
	
	private static void graphRequest(String graphPath){
		fbr.request(graphPath, new GFacebookRequest());
	}
	
	private static void graphRequest(String graphPath, Object paramaters){
		fbr.request(graphPath, (Bundle)paramaters, new GFacebookRequest());
	}
	
	private static void graphRequest(String graphPath, Object paramaters, String method){	
		fbr.request(graphPath, (Bundle)paramaters, method, new GFacebookRequest(), true);
	}
	
	private static void setAccessToken(String accessToken){
		fb.setAccessToken(accessToken);
	}
	
	private static String getAccessToken(){
		return fb.getAccessToken();
	}
	
	private static void setExpirationDate(long timestampInMsec){
		fb.setAccessExpires(timestampInMsec);
	}
	
	private static long getExpirationDate(){
		return fb.getAccessExpires();
	}
	
	private static void extendAccessToken(){
		fb.extendAccessToken(sActivity.get(), new GFacebookService());
	}
	
	private static void extendAccessTokenIfNeeded(){
		fb.extendAccessTokenIfNeeded(sActivity.get(), new GFacebookService());
	}
	
	private static boolean shouldExtendAccessToken(){
		return fb.shouldExtendAccessToken();
	}
	
	public static void saveFBToken(String token, long tokenExpires){
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sActivity.get());
	    prefs.edit().putString("FacebookToken", token).putLong("FacebookTokenExpires", tokenExpires).commit();
	}
	
	public static String getFBToken(){
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sActivity.get());
	    return prefs.getString("FacebookToken", "");
	}
	
	public static long getFBTokenExpires(){
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sActivity.get());
	    return prefs.getLong("FacebookTokenExpires", 0);
	}
	public static void deleteFBToken(){
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sActivity.get());
	    prefs.edit().remove("FacebookToken").remove("FacebookTokenExpires").commit();
	}
	
	public static native void onLoginComplete(long data);
	public static native void onLoginError(long data);
	public static native void onLoginCancel(long data);
	public static native void onLogoutComplete(long data);
	public static native void onDialogComplete(long data);
	public static native void onDialogError(int errorCode, String errorDescription, long data);
	public static native void onDialogCancel(long data);
	public static native void onRequestComplete(String response, int length, long data);
	public static native void onRequestError(int errorCode, String errorDescription, long data);

}

@SuppressWarnings("deprecation")
class GFacebookAuth implements DialogListener
{

	@Override
	public void onComplete(Bundle values) {
		Log.d("GFacebook","Facebook.authorize Complete: ");
		GFacebook.saveFBToken(GFacebook.fb.getAccessToken(), GFacebook.fb.getAccessExpires());
		if (GFacebook.sData != 0)
    	{
			GFacebook.onLoginComplete(GFacebook.sData);
    	}
	}

	@Override
	public void onFacebookError(FacebookError e) {
		 Log.d("GFacebook","Facebook.authorize Error: "+e.toString());
		 if (GFacebook.sData != 0)
		 {
			 GFacebook.onLoginError(GFacebook.sData);
		 }
		
	}

	@Override
	public void onError(DialogError e) {
		Log.d("GFacebook","Facebook.authorize DialogError: "+e.toString());
		if (GFacebook.sData != 0)
		{
			GFacebook.onLoginError(GFacebook.sData);
		}
		
	}

	@Override
	public void onCancel() {
		Log.d("GFacebook","Facebook authorization canceled");
		if (GFacebook.sData != 0)
		{
			GFacebook.onLoginCancel(GFacebook.sData);
		}
		
	}
	
}

@SuppressWarnings("deprecation")
class GFacebookDialog implements DialogListener
{

	@Override
	public void onComplete(Bundle values) {
		Log.d("GFacebook","Facebook.authorize Complete: ");
        //saveFBToken(GFacebook.fb.getAccessToken(), GFacebook.fb.getAccessExpires());
		if (GFacebook.sData != 0)
    	{
			GFacebook.onDialogComplete(GFacebook.sData);
    	}
	}

	@Override
	public void onFacebookError(FacebookError e) {
		 Log.d("GFacebook","Facebook.authorize Error: "+e.toString());
		 if (GFacebook.sData != 0)
		 {
			 GFacebook.onDialogError(e.getErrorCode(), e.getLocalizedMessage(), GFacebook.sData);
		 }
		
	}

	@Override
	public void onError(DialogError e) {
		Log.d("GFacebook","Facebook.authorize DialogError: "+e.toString());
		if (GFacebook.sData != 0)
    	{
			GFacebook.onDialogError(e.getErrorCode(), e.getLocalizedMessage(), GFacebook.sData);
    	}
		
	}

	@Override
	public void onCancel() {
		Log.d("GFacebook","Facebook authorization canceled");
		if (GFacebook.sData != 0)
    	{
			GFacebook.onDialogCancel(GFacebook.sData);
    	}
		
	}
	
}

@SuppressWarnings("deprecation")
class GFacebookRequest implements RequestListener
{

	@Override
	public void onComplete(String response, Object state) {
		Log.d("GFacebook","Request complete: " + response);
		if (GFacebook.sData != 0)
    	{
			GFacebook.onRequestComplete(response, response.length(), GFacebook.sData);
    	}
		
	}

	@Override
	public void onIOException(IOException e, Object state) {
		Log.d("GFacebook","IOException");
		if (GFacebook.sData != 0)
    	{
			GFacebook.onRequestError(1, e.getLocalizedMessage(), GFacebook.sData);
    	}
		
	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		Log.d(this.getClass().getName(),"FileNotFoundException");
		if (GFacebook.sData != 0)
    	{
			GFacebook.onRequestError(2, e.getLocalizedMessage(), GFacebook.sData);
    	}
		
	}

	@Override
	public void onMalformedURLException(MalformedURLException e, Object state) {
		Log.d("GFacebook","MalformedURLException");
		if (GFacebook.sData != 0)
    	{
			GFacebook.onRequestError(3, e.getLocalizedMessage(), GFacebook.sData);
    	}
	}

	@Override
	public void onFacebookError(FacebookError e, Object state) {
		Log.d("GFacebook","Request FaceBook error");
		if (GFacebook.sData != 0)
    	{
			GFacebook.onRequestError(e.getErrorCode(), e.getLocalizedMessage(), GFacebook.sData);
    	}
		
	}
	
}

@SuppressWarnings("deprecation")
class GFacebookLogout implements RequestListener
{

	@Override
	public void onComplete(String response, Object state) {
		Log.d("GFacebook","Request complete: " + response);
		if (GFacebook.sData != 0)
    	{
			GFacebook.deleteFBToken();
			GFacebook.onLogoutComplete(GFacebook.sData);
    	}
		
	}

	@Override
	public void onIOException(IOException e, Object state) {}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {}

	@Override
	public void onMalformedURLException(MalformedURLException e, Object state) {}

	@Override
	public void onFacebookError(FacebookError e, Object state) {}
	
}

class GFacebookService implements ServiceListener
{

	@Override
	public void onComplete(Bundle values) {
		Log.d("GFacebook","Service complete");
	}

	@Override
	public void onFacebookError(FacebookError e) {
		Log.d("GFacebook","Service Facebook Error");
	}

	@Override
	public void onError(Error e) {
		Log.d("GFacebook","Service Error");
	}

}
