package com.ata.rfiddemo.SaveList;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ata.rfiddemo.MainActivity;
import com.ata.rfiddemo.R;
import com.ata.rfiddemo.SaveList.Fragment.SaveListDetailFragment;
import com.ata.rfiddemo.SaveList.Fragment.SaveListFragment;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;

import java.util.Stack;

public class SaveListActivity extends ToolbarManager {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private SaveListFragment saveListFragment;
    private SaveListDetailFragment saveListDetailFragment;
    private Stack<Fragment> fragmentStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_list);

        fragmentStack    = new Stack<>();
        saveListFragment = new SaveListFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout_saveList, saveListFragment, "saveList");
        fragmentTransaction.commit();
    }

    public void replaceFragment(String tag){
        replaceFragment(tag, null);
    }

    public void replaceFragment(String tag, Bundle bundle) {
        fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout_saveList);
        fragmentStack.push(currentFragment);
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = null;

        switch (tag){
            case "saveList":
                if (saveListFragment == null) {
                    saveListFragment = new SaveListFragment();
                }
                fragment = saveListFragment;
                break;

            case "saveListDetail":
                if (saveListDetailFragment == null) {
                    saveListDetailFragment = new SaveListDetailFragment();
                }
                fragment = saveListDetailFragment;
                break;
        }

        if(bundle != null){
            fragment.setArguments(bundle);
        }

        fragmentTransaction.replace(R.id.frameLayout_saveList, fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragmentStack.isEmpty()) {
            Fragment nextFragment = fragmentStack.pop();

            fragmentManager.beginTransaction().replace(R.id.frameLayout_saveList, nextFragment).commit();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
