package com.swmansion.rnscreens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;

public class ScreenFragment extends Fragment {

  protected Screen mScreenView;

  public ScreenFragment() {
    throw new IllegalStateException("Screen fragments should never be restored");
  }

  @SuppressLint("ValidFragment")
  public ScreenFragment(Screen screenView) {
    super();
    mScreenView = screenView;
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return mScreenView;
  }

  protected ViewGroup getScreenRootView() {
    return mScreenView;
  }

  public Screen getScreen() {
    return mScreenView;
  }

  private void dispatchOnAppear() {
    ((ReactContext) mScreenView.getContext())
            .getNativeModule(UIManagerModule.class)
            .getEventDispatcher()
            .dispatchEvent(new ScreenAppearEvent(mScreenView.getId()));
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ScreenContainer container = view.getContainer();
    if (container != null && container.isTransitioning()) {
      container.postAfterTransition(new Runnable() {
        @Override
        public void run() {
          dispatchOnAppear();
        }
      });
    } else {
      dispatchOnAppear();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    ScreenContainer container = mScreenView.getContainer();
    if (container == null || !container.hasScreen(this)) {
      // we only send dismissed even when the screen has been removed from its container
      ((ReactContext) mScreenView.getContext())
              .getNativeModule(UIManagerModule.class)
              .getEventDispatcher()
              .dispatchEvent(new ScreenDismissedEvent(mScreenView.getId()));
    }
  }
}
