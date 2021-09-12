package com.example.asus.chatbloom;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccesorAdapter extends FragmentPagerAdapter {
    public TabsAccesorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch(i) {
            case 0:
                ChatFragment chatsFragment = new ChatFragment();
                return chatsFragment;

            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            case 2:
                RequestsFragment requestFragment = new RequestsFragment();
                return requestFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "chats";

            case 1:
                return "contacts";

            case 2:
                return "Requests";

            default:
                return null;
        }
    }
}
