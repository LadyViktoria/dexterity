package com.ht1.dexterity.app;


import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.List;

/**
 * Created by John Costik on 6/6/14.
 */
public class ReceiverUsbHostFragment extends Fragment
{
//  private UsbSerialDriver mSerial;
//  private UsbManager mUsbManager;
//  boolean mBound = false;
//	private DexterityUsbReceiverService mService;

    private TextView mUsbDeviceName;
    private TextView mSocketDebug;
    private View mRootView;
	private DataUpdateReceiver dataUpdateReceiver;
	private DexterityDataSource mDataSource;


/*
    private ServiceConnection mConnection= new ServiceConnection()
	{
        public void onServiceConnected(ComponentName className, IBinder service)
		{
            DexterityUsbReceiverService.DexterityUsbServiceBinder binder = (DexterityUsbReceiverService.DexterityUsbServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className)
		{
			getActivity().unbindService(this);
			mBound = false;
        }
    };
*/

    private class DataUpdateReceiver extends BroadcastReceiver
	{
        @Override
        public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals("USB_CONNECT"))
			{
//				final ToggleButton button = (ToggleButton) mRootView.findViewById(R.id.toggleButton);
//				button.setChecked(true);
				mUsbDeviceName.setText("Connected");
			}
            else if (intent.getAction().equals("USB_DISCONNECT"))
			{
//                final ToggleButton button = (ToggleButton) mRootView.findViewById(R.id.toggleButton);
//                button.setChecked(false);
                mUsbDeviceName.setText("Disconnected");
            }
            else if (intent.getAction().equals("NEW_PRINT")){
            	mSocketDebug.setText(/*ServerSockets.mDebugString*/ "DebugString");
            }
			else if (intent.getAction().equals("NEW_READ"))
			{
                refreshListView();
            }
        }
    }

    public static ReceiverUsbHostFragment newInstance(int sectionNumber)
	{
        ReceiverUsbHostFragment fragment = new ReceiverUsbHostFragment();
        return fragment;
    }

    public ReceiverUsbHostFragment()
	{
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        LayoutInflater lf = getActivity().getLayoutInflater();
        View rootView = lf.inflate(R.layout.fragment_receiver_usb, container, false);
        mUsbDeviceName = (TextView) rootView.findViewById(R.id.textView4);
        mSocketDebug = (TextView) rootView.findViewById(R.id.SocketViewDebug);
        mRootView = rootView;
        refreshListView();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
	{
        super.onAttach(activity);
	}

    @Override
    public void onStart()
	{
        super.onStart();
//        Intent intent = new Intent(getActivity().getApplicationContext(), DexterityUsbReceiverService.class);
//        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

	/*

    private void doBindService()
	{
        Intent intent = new Intent(getActivity().getApplicationContext(), DexterityUsbReceiverService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    */

    @Override
    public void onResume()
	{
        super.onResume();

        //doBindService();

        if (dataUpdateReceiver == null)
			dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("USB_CONNECT");
        intentFilter.addAction("USB_DISCONNECT");
        intentFilter.addAction("NEW_READ");
        intentFilter.addAction("NEW_PRINT");
        getActivity().registerReceiver(dataUpdateReceiver, intentFilter);

        refreshListView();

		/*
        //Bind to Service Start Service
        mUsbManager = (UsbManager) getActivity().getBaseContext().getSystemService(Context.USB_SERVICE);
        mSerial = UsbSerialProber.findFirstDevice(mUsbManager);

        if (mSerial!=null)
		{
			button.setChecked(true);
            mUsbDeviceName.setText("Connected");
            if (mService != null)
                mService.startSerialRead();
			else
				mUsbDeviceName.setText("Connected but no mService!");
        }
		else
			button.setChecked(false);

		final ToggleButton button = (ToggleButton) mRootView.findViewById(R.id.toggleButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
				if(button.isChecked())
				{
					mService.startSerialRead();
				}
				else
				{
					mService.stopSerialRead(new Intent(getActivity().getApplicationContext(), DexterityUsbReceiverService.class));
				}
            }
        });
		*/


//        mUsbManager = null;
//        mSerial = null;
    }

    @Override
    public void onPause()
	{
        super.onPause();
        if (dataUpdateReceiver != null)
			getActivity().unregisterReceiver(dataUpdateReceiver);
    }

    @Override
    public void onStop()
	{
        super.onStop();
    }

    private void refreshListView()
	{
        mDataSource = new DexterityDataSource(getActivity());
        mDataSource.open();
        List<String> values = mDataSource.getAllData();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
        ListView list = (ListView)mRootView.findViewById(R.id.listView);
        list.setAdapter(adapter);
        mDataSource.close();
    }






}
