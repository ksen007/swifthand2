package swifthand;

// Import the uiautomator libraries

import android.os.Message;
import android.view.accessibility.AccessibilityNodeInfo;

import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;


interface Action {
    public void action();
}

class ActionPair {
    String actionName;
    Action action;

    ActionPair(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }
}

public class SwiftHand extends UiAutomatorTestCase {
    private String currentAppPackageName = null;
    private Random rand = new Random();
    final public static int TIMEOUT = 2000;

    private ActionPair[] actions = new ActionPair[]{
            new ActionPair("back", new Action() {
                @Override
                public void action() {
                    getUiDevice().pressBack();
                    getUiDevice().waitForIdle(TIMEOUT);
                }
            }),
            new ActionPair("menu", new Action() {
                @Override
                public void action() {
                    getUiDevice().pressMenu();
                    getUiDevice().waitForIdle(TIMEOUT);
                }
            })
    };

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
        getUiDevice().waitForIdle(TIMEOUT);
    }

    private String getCurrentAppPackageName() throws UiObjectNotFoundException {
        UiObject root = new UiObject(new UiSelector().index(0));
        return root.getPackageName();
    }

    private boolean isInSameApp() throws UiObjectNotFoundException {
        String name = getCurrentAppPackageName();
        if (currentAppPackageName == null) {
            currentAppPackageName = name;
            System.out.println("Setting currentAppPackageName " + currentAppPackageName);
            return true;
        } else {
            System.out.println("currentAppPackageName "
                    + currentAppPackageName
                    + " actualAppPackageName "
                    + name);
            return currentAppPackageName.equals(name);
        }
    }

    private void addClickables(UiCollection root, LinkedList sb) throws UiObjectNotFoundException {
        int count = root.getChildCount(new UiSelector()
                .classNameMatches(".*")
                .clickable(true)
                .enabled(true));
        for (int i = 0; i < count; i++) {
            UiObject uiobj = new UiObject(new UiSelector()
                    .classNameMatches(".*")
                    .clickable(true)
                    .enabled(true)
                    .instance(i));
            sb.addLast("click"
                    + ":"
                    + i
                    + ":"
                    + uiobj.getClassName()
                    + ":"
                    + uiobj.getBounds().flattenToString());
        }
    }

    private void addLongClickables(UiCollection root, LinkedList sb) throws UiObjectNotFoundException {
        int count = root.getChildCount(new UiSelector()
                .classNameMatches(".*")
                .longClickable(true)
                .enabled(true));
        for (int i = 0; i < count; i++) {
            UiObject uiobj = new UiObject(new UiSelector()
                    .classNameMatches(".*")
                    .longClickable(true)
                    .enabled(true)
                    .instance(i));
            sb.addLast("long"
                    + ":"
                    + i
                    + ":"
                    + uiobj.getClassName()
                    + ":"
                    + uiobj.getBounds().flattenToString());
        }

    }

    private void addCheckables(UiCollection root, LinkedList sb) throws UiObjectNotFoundException {
        int count = root.getChildCount(new UiSelector()
                .classNameMatches(".*")
                .checkable(true)
                .enabled(true));
        for (int i = 0; i < count; i++) {
            UiObject uiobj = new UiObject(new UiSelector()
                    .classNameMatches(".*")
                    .checkable(true)
                    .enabled(true)
                    .instance(i));
            sb.addLast("check"
                    + ":"
                    + i
                    + ":"
                    + uiobj.getClassName()
                    + ":" + uiobj.getBounds().flattenToString()
                    + ":" + uiobj.isChecked());
        }
    }

    private void addScollables(UiCollection root, LinkedList sb) throws UiObjectNotFoundException {
        int count = root.getChildCount(new UiSelector()
                .classNameMatches(".*")
                .scrollable(true)
                .enabled(true));
        for (int i = 0; i < count; i++) {
            UiObject uiobj = new UiObject(new UiSelector()
                    .classNameMatches(".*")
                    .scrollable(true)
                    .enabled(true)
                    .instance(i));
            sb.addLast("scroll"
                    + ":"
                    + i
                    + ":"
                    + uiobj.getClassName()
                    + ":"
                    + uiobj.getBounds().flattenToString());
        }

    }

    private void addTextWidgets(UiCollection root, LinkedList sb) throws UiObjectNotFoundException {
        int count = root.getChildCount(new UiSelector()
                .textMatches(".+"));
        for (int i = 0; i < count; i++) {
            UiObject uiobj = new UiObject(new UiSelector()
                    .textMatches(".+")
                    .instance(i));
            sb.addLast("text:"
                    + i
                    + ":"
                    + uiobj.getClassName()
                    + ":"
                    + uiobj.getBounds().flattenToString()
                    + ":"
                    + uiobj.getText());
        }


    }

    private LinkedList getAbstractUIState() throws UiObjectNotFoundException {
        LinkedList sb = new LinkedList();
        int attempts = 1;

        while(true) {
            try {
                UiCollection root = new UiCollection(new UiSelector().index(0));

                for (int i = 0; i < actions.length; i++) {
                    ActionPair action = actions[i];
                    sb.addLast(action.actionName);
                }

                addClickables(root, sb);

                addLongClickables(root, sb);

                addCheckables(root, sb);

                addScollables(root, sb);

                return sb;
            } catch (Exception e) {
                attempts++;
                sb.clear();
                System.err.println("-----------------------");
                if (attempts>2) {
                    System.err.println("Needs attention !!!!!!!!!");
                    e.printStackTrace();
                }
                System.err.println("----------------------- Trying to retrieve UI State again: attempt number " + attempts);
                getUiDevice().waitForIdle(TIMEOUT);
            }
        }
    }

    private void clickAction(String instance) throws UiObjectNotFoundException {
        int i = Integer.parseInt(instance);
        UiObject uiobj = new UiObject(new UiSelector()
                .classNameMatches(".*")
                .clickable(true)
                .enabled(true)
                .instance(i));
        String cname = uiobj.getClassName();
        System.out.println("Clicking "
                + cname
                + " at "
                + uiobj.getBounds().flattenToString());
        uiobj.click();
        getUiDevice().waitForIdle(TIMEOUT);
        if (cname.equals("android.widget.EditText")) {
            uiobj.setText("random text");
        }
        getUiDevice().waitForIdle(TIMEOUT);
    }

    private void longClickAction(String instance) throws UiObjectNotFoundException {
        int i = Integer.parseInt(instance);
        UiObject uiobj = new UiObject(new UiSelector()
                .classNameMatches(".*")
                .longClickable(true)
                .enabled(true)
                .instance(i));
        System.out.println("Long clicking "
                + uiobj.getClassName()
                + " at "
                + uiobj.getBounds().flattenToString());
        uiobj.longClick();
        getUiDevice().waitForIdle(TIMEOUT);
    }

    private void checkAction(String instance) throws UiObjectNotFoundException {
        int i = Integer.parseInt(instance);
        UiObject uiobj = new UiObject(new UiSelector()
                .classNameMatches(".*")
                .checkable(true)
                .enabled(true)
                .instance(i));
        System.out.println("Checking "
                + uiobj.getClassName()
                + " at "
                + uiobj.getBounds().flattenToString());
        uiobj.click();
        getUiDevice().waitForIdle(TIMEOUT);

    }

    private void scrollAction(String instance) throws UiObjectNotFoundException {
        int i = Integer.parseInt(instance);
        UiScrollable uiobj = new UiScrollable(new UiSelector()
                .classNameMatches(".*")
                .scrollable(true)
                .enabled(true)
                .instance(i));
        System.out.println("Scrolling "
                + uiobj.getClassName()
                + " at "
                + uiobj.getBounds().flattenToString());
        uiobj.scrollForward();
        getUiDevice().waitForIdle(TIMEOUT);

    }

    private void triggerEvent(String event) throws UiObjectNotFoundException {
        try {
            for (int i = 0; i < actions.length; i++) {
                ActionPair action = actions[i];
                if (event.equals(action.actionName)) {
                    action.action.action(); // I know that this looks funny and annoying
                }
            }

            String[] components = event.split(":");
            if (components[0].equals("click")) {
                clickAction(components[1]);
            } else if (components[0].equals("long")) {
                longClickAction(components[1]);
            } else if (components[0].equals("check")) {
                checkAction(components[1]);
            } else if (components[0].equals("scroll")) {
                scrollAction(components[1]);
            } else if (components[0].equals("launch")) {
                launchApp(components[1]);
            }
        } catch (Exception e) {
            System.err.println("-----------------------");
            e.printStackTrace();
            System.err.println(getAbstractUIState());
            System.err.println("-----------------------");
        }
    }

    private String getRandomEvent(LinkedList elist) {
        String evt;
        int i = rand.nextInt(elist.size());
        while ((evt = (String) elist.get(i)).startsWith("text")) {
            i = rand.nextInt(i);
        }
        return evt;
    }

    private void clickOnARandomView() throws UiObjectNotFoundException {
        UiCollection root = new UiCollection(new UiSelector().index(0));
        int count = root.getChildCount(new UiSelector().classNameMatches(".*").clickable(true).enabled(true));
        System.out.println("Count " + count);
        int i = rand.nextInt(count);
        UiObject clickable = new UiObject(new UiSelector().classNameMatches(".*").clickable(true).enabled(true).instance(i));
        System.out.println("Clicking " + clickable.getClassName() + " at " + clickable.getBounds().flattenToString());
        clickable.click();
        getUiDevice().waitForIdle(TIMEOUT);
    }

    public void testDemo() throws UiObjectNotFoundException, IOException {
        ServerSocket serverSocket = new ServerSocket(Constants.SERVERPORT);
        boolean loop = true;

        while (loop) {
            // each iteration represents a client
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));


            while(true) {
                // each iteration represents an event from the client

                // ui state
                LinkedList elist = getAbstractUIState();
                System.out.println(elist);

                // send ui state
                out.println(getCurrentAppPackageName());
                for (Object line : elist) {
                    out.println(line);
                }
                out.println("end");

                // get event
                String event = in.readLine();

                if (event == null || event.equals("end")) {
                    break;
                }
                if (event.equals("shutdown")) {
                    loop = false;
                    break;
                }
                // trigger event
                triggerEvent(event);
            }

            clientSocket.close();
        }
        serverSocket.close();
    }


}

