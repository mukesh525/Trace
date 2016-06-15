// Generated code from Butter Knife. Do not modify!
package vmc.in.mrecorder.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class Feedback$$ViewInjector<T extends vmc.in.mrecorder.activity.Feedback> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624114, "field 'etFeedback'");
    target.etFeedback = finder.castView(view, 2131624114, "field 'etFeedback'");
    view = finder.findRequiredView(source, 2131624115, "field 'button'");
    target.button = finder.castView(view, 2131624115, "field 'button'");
    view = finder.findRequiredView(source, 2131624112, "field 'mroot'");
    target.mroot = finder.castView(view, 2131624112, "field 'mroot'");
  }

  @Override public void reset(T target) {
    target.etFeedback = null;
    target.button = null;
    target.mroot = null;
  }
}
