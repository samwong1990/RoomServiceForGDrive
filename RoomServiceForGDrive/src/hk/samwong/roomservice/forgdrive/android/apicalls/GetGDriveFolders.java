package hk.samwong.roomservice.forgdrive.android.apicalls;

import hk.samwong.roomservice.android.library.apicalls.APICaller;
import hk.samwong.roomservice.android.library.constants.HttpVerb;
import hk.samwong.roomservice.android.library.constants.LogTag;
import hk.samwong.roomservice.android.library.fingerprintCollection.WifiScanner;
import hk.samwong.roomservice.android.library.helpers.AuthenticationDetailsPreperator;
import hk.samwong.roomservice.commons.dataFormat.AuthenticationDetails;
import hk.samwong.roomservice.commons.dataFormat.WifiInformation;
import hk.samwong.roomservice.commons.parameterEnums.ReturnCode;
import hk.samwong.roomservice.forgdrive.android.constants.Defaults;
import hk.samwong.roomservice.forgdrive.commons.dataFormat.GDriveFolder;
import hk.samwong.roomservice.forgdrive.commons.dataFormat.ResponseWithGDriveFolders;
import hk.samwong.roomservice.forgdrive.commons.enums.GDriveOperation;
import hk.samwong.roomservice.forgdrive.commons.enums.GDriveParameterKey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class GetGDriveFolders extends APICaller<Activity, Void, List<GDriveFolder>> {

	public GetGDriveFolders(Activity context) {
		super(context);
		SERVLET_URL = Defaults.GDRIVE_SERVLET_URL;
	}

	@Override
	protected List<GDriveFolder> doInBackground(Activity... params) {
		List<GDriveFolder> temp = new LinkedList<GDriveFolder>();
		Random r = new Random();
		for(int i=0; i<15; i++){
			temp.add(new GDriveFolder().withUrl(r.nextInt()+"").withBrains(r.nextInt()).withInviteOnly(r.nextBoolean()).withName(r.nextInt()+"").withOwner(r.nextInt()+"").withRoom(r.nextInt()+"").withStarred(r.nextBoolean()).withUrl(r.nextInt()+""));
		}
		if(true) return temp;
		HttpURLConnection urlConnection = null;
		try {
			AuthenticationDetails authenticationDetails = new AuthenticationDetailsPreperator().getAuthenticationDetails(getContext());

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(GDriveParameterKey.OPERATION.toString(), GDriveOperation.GetGDriveFolders.toString()));
			nvps.add(new BasicNameValuePair(GDriveParameterKey.AUENTICATION_DETAILS.toString(), AuthenticationDetailsPreperator
					.getAuthenticationDetailsAsJson(authenticationDetails)));
			WifiInformation wifiscan = WifiScanner.getWifiInformation(params[0]);
			nvps.add(new BasicNameValuePair(GDriveParameterKey.OBSERVATION.toString(), new Gson().toJson(wifiscan)));

			try {
				String result = getJsonResponseFromAPICall(HttpVerb.POST, nvps);
				ResponseWithGDriveFolders response = new Gson().fromJson(result, new TypeToken<ResponseWithGDriveFolders>() {
				}.getType());
				if (response.getReturnCode().equals(ReturnCode.OK)) {
					return response.getGDriveFolders();
				} else {
					throw new IOException(response.getExplanation());
				}
			} catch (Exception e) {
				addException(e);
			}
			Log.w(LogTag.APICALL.toString(), "No response for the GetGDriveFolders query");
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return null;
	}

	abstract protected void onPostExecute(List<GDriveFolder> result);

}
