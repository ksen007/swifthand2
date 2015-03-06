package swifthand;

// Import the uiautomator libraries

import android.view.accessibility.AccessibilityNodeInfo;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import java.util.LinkedList;
import java.util.Random;

public class SwiftHand extends UiAutomatorTestCase {
    final public static String TAG = "SwiftHand";
    private String currentAppPackageName = null;
    private Random rand = new Random();
    final public static int TIMEOUT = 2000;

    private void launchApp(String appName) throws UiObjectNotFoundException {
        getUiDevice().pressHome();
        UiObject allAppsButton = new UiObject(new UiSelector()
                .description("Apps"));
        allAppsButton.clickAndWaitForNewWindow();
        UiObject appsTab = new UiObject(new UiSelector()
                .text("Apps"));
        appsTab.click();
        UiScrollable appViews = new UiScrollable(new UiSelector()
                .scrollable(true));
        appViews.setAsHorizontalList();
        UiObject settingsApp = appViews.getChildByText(new UiSelector()
                        .className(android.widget.TextView.class.getName()),
                appName);
        settingsApp.clickAndWaitForNewWindow();
    }

    private boolean isInSameApp() throws UiObjectNotFoundException {
        UiObject root = new UiObject(new UiSelector().index(0));
        if (currentAppPackageName == null) {
            currentAppPackageName = root.getPackageName();
            return true;
        } else {
            System.out.println("currentAppPackageName "+currentAppPackageName+" actualAppPackageName "+root.getPackageName());
            return currentAppPackageName.equals(root.getPackageName());
        }
    }

    private LinkedList getAbstractUIState() throws UiObjectNotFoundException {
        LinkedList sb = new LinkedList();
        UiCollection root = new UiCollection(new UiSelector().index(0));
        int count;

        count = root.getChildCount(new UiSelector().classNameMatches(".*").clickable(true).enabled(true));
        for (int i=0; i<count; i++) {
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").clickable(true).enabled(true).instance(i));
            sb.addLast("click"+":"+i+":"+uiobj.getClassName()+":"+uiobj.getBounds().flattenToString());
        }

        count = root.getChildCount(new UiSelector().classNameMatches(".*").longClickable(true).enabled(true));
        for (int i=0; i<count; i++) {
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").longClickable(true).enabled(true).instance(i));
            sb.addLast("long" + ":" + i + ":" + uiobj.getClassName() + ":" + uiobj.getBounds().flattenToString());
        }

        count = root.getChildCount(new UiSelector().classNameMatches(".*").checkable(true).enabled(true));
        for (int i=0; i<count; i++) {
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").checkable(true).enabled(true).instance(i));
            sb.addLast("check" + ":" + i + ":" + uiobj.getClassName() + ":" + uiobj.getBounds().flattenToString() + ":" + uiobj.isChecked());
        }

        count = root.getChildCount(new UiSelector().classNameMatches(".*").scrollable(true).enabled(true));
        for (int i=0; i<count; i++) {
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").scrollable(true).enabled(true).instance(i));
            sb.addLast("scroll" + ":" + i + ":" + uiobj.getClassName() + ":" + uiobj.getBounds().flattenToString());
        }

        count = root.getChildCount(new UiSelector().textMatches(".+"));
        for (int i=0; i<count; i++) {
            UiObject uiobj = new UiObject(new UiSelector().textMatches(".+").instance(i));
            sb.addLast("text:" + i + ":" + uiobj.getClassName() + ":" + uiobj.getBounds().flattenToString() + ":" + uiobj.getText());
        }

        return sb;
    }

    private void triggerEvent(String event) throws UiObjectNotFoundException {
        String[] components = event.split(":");
        if (components[0].equals("click")) {
            int i = Integer.parseInt(components[1]);
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").clickable(true).enabled(true).instance(i));
            System.out.println("Clicking " + uiobj.getClassName() + " at " + uiobj.getBounds().flattenToString());
            uiobj.click();
            getUiDevice().waitForIdle(2000);
        } else if (components[0].equals("long")) {
            int i = Integer.parseInt(components[1]);
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").longClickable(true).enabled(true).instance(i));
            System.out.println("Long clicking " + uiobj.getClassName() + " at " + uiobj.getBounds().flattenToString());
            uiobj.longClick();
            getUiDevice().waitForIdle(2000);
        } else if (components[0].equals("check")) {
            int i = Integer.parseInt(components[1]);
            UiObject uiobj = new UiObject(new UiSelector().classNameMatches(".*").checkable(true).enabled(true).instance(i));
            System.out.println("Checking " + uiobj.getClassName() + " at " + uiobj.getBounds().flattenToString());
            uiobj.click();
            getUiDevice().waitForIdle(2000);
        }
    }

    private void clickOnARandomView() throws UiObjectNotFoundException {
        UiCollection root = new UiCollection(new UiSelector().index(0));
        int count = root.getChildCount(new UiSelector().classNameMatches(".*").clickable(true).enabled(true));
        System.out.println("Count " + count);
        int i = rand.nextInt(count);
        UiObject clickable = new UiObject(new UiSelector().classNameMatches(".*").clickable(true).enabled(true).instance(i));
        System.out.println("Clicking " + clickable.getClassName() + " at " + clickable.getBounds().flattenToString());
        clickable.click();
        getUiDevice().waitForIdle(2000);
    }

    public void testDemo() throws UiObjectNotFoundException {
        String appName = "Settings";
        launchApp(appName);
        System.out.println(getAbstractUIState());
        for (int i=0; i<10; i++) {
            if (!isInSameApp()) {
                launchApp(appName);
                System.out.println(getAbstractUIState());
            }
            clickOnARandomView();
            System.out.println(getAbstractUIState());
        }
    }
}

