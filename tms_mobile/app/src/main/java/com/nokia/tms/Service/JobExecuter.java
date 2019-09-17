package com.nokia.tms.Service;

import android.os.AsyncTask;

public class JobExecuter extends AsyncTask<Void,Void,String> {
    @Override
    protected String doInBackground(Void... voids) {
        return "Task Finished";
    }
}
