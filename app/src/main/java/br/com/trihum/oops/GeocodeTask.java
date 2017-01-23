package br.com.trihum.oops;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import br.com.trihum.oops.fragment.PrincipalFragment;
import br.com.trihum.oops.model.InfracaoComDetalhe;

/**
 * Created by raphaelmoraes on 15/01/17.
 */

public class GeocodeTask extends AsyncTask<String, Integer, String> {

    private double latitude;
    private double longitude;
    private Activity activity;
    private PrincipalFragment fragment;
    private InfracaoComDetalhe infracaoComDetalhe;
    private String endereco;
    public boolean isRunning;

    public GeocodeTask(Activity activity, double latitude, double longitude)
    {
        this.activity = activity;
        this.latitude = latitude;
        this.longitude = longitude;
        isRunning = false;
    }

    public GeocodeTask(PrincipalFragment fragment, InfracaoComDetalhe infracaoComDetalhe)
    {
        this.fragment = fragment;
        this.infracaoComDetalhe = infracaoComDetalhe;
        this.latitude = infracaoComDetalhe.getLatitude();
        this.longitude = infracaoComDetalhe.getLongitude();
        isRunning = false;
    }

    @Override
    protected void onPreExecute()
    {
        isRunning = true;
    }

    @Override
    protected String doInBackground(String... strings) {

        Geocoder geoCoder;
        if (activity!=null)
        {
            geoCoder = new Geocoder(activity);
        }
        else
        {
            geoCoder = new Geocoder(fragment.getContext());
        }

        try
        {
            List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            endereco = bestMatch.getAddressLine(0)+" "+bestMatch.getLocality()+" "+bestMatch.getAdminArea();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return endereco;
    }

    @Override
    protected void onPostExecute(String result)
    {
        isRunning = false;
        if (activity!=null)
        {
            ((RegistraInfracaoActivity)activity).atualizaInfoEndereco(result);
        }
        else if (fragment!=null)
        {
            infracaoComDetalhe.setEndereco(result);
            ((PrincipalFragment)fragment).atualizaEnderecoEnviaOffline(infracaoComDetalhe);
        }
    }
}
