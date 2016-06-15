// Generated code from Butter Knife. Do not modify!
package vmc.in.mrecorder.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ForgotPasword$$ViewInjector<T extends vmc.in.mrecorder.activity.ForgotPasword> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624117, "field '_phone'");
    target._phone = finder.castView(view, 2131624117, "field '_phone'");
    view = finder.findRequiredView(source, 2131624119, "field '_sendOTp'");
    target._sendOTp = finder.castView(view, 2131624119, "field '_sendOTp'");
    view = finder.findRequiredView(source, 2131624121, "field '_repassword'");
    target._repassword = finder.castView(view, 2131624121, "field '_repassword'");
    view = finder.findRequiredView(source, 2131624120, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131624120, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131624122, "field '_changePassButton'");
    target._changePassButton = finder.castView(view, 2131624122, "field '_changePassButton'");
    view = finder.findRequiredView(source, 2131624095, "field '_loginLink'");
    target._loginLink = finder.castView(view, 2131624095, "field '_loginLink'");
    view = finder.findRequiredView(source, 2131624118, "field '_fgOTP'");
    target._fgOTP = finder.castView(view, 2131624118, "field '_fgOTP'");
    view = finder.findRequiredView(source, 2131624116, "field 'mroot'");
    target.mroot = finder.castView(view, 2131624116, "field 'mroot'");
  }

  @Override public void reset(T target) {
    target._phone = null;
    target._sendOTp = null;
    target._repassword = null;
    target._passwordText = null;
    target._changePassButton = null;
    target._loginLink = null;
    target._fgOTP = null;
    target.mroot = null;
  }
}
