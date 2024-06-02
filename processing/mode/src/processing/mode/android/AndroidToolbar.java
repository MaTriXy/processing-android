/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
 Part of the Processing project - http://processing.org

 Copyright (c) 2012-21 The Processing Foundation
 Copyright (c) 2011-12 Ben Fry and Casey Reas

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 2
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package processing.mode.android;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import processing.app.Base;
import processing.app.ui.Editor;
import processing.app.ui.EditorButton;
import processing.app.ui.EditorToolbar;
import processing.app.Language;

import javax.swing.*;


@SuppressWarnings("serial")
public class AndroidToolbar extends EditorToolbar {
  static protected final int RUN_ON_DEVICE   = 0;
  static protected final int RUN_IN_EMULATOR = 1;
  static protected final int STOP            = 2;

  static protected final int NEW             = 3;
  static protected final int OPEN            = 4;
  static protected final int SAVE            = 5;

  static protected final int EXPORT_PACKAGE = 6;  
  static protected final int EXPORT_BUNDLE  = 7;
  static protected final int EXPORT_PROJECT = 8;


  private AndroidEditor aEditor;

  EditorButton stepButton;
  EditorButton continueButton;

  public AndroidToolbar(Editor editor, Base base) {
    super(editor);
    aEditor = (AndroidEditor) editor;
  }


  static public String getTitle(int index) {
    switch (index) {
    case RUN_ON_DEVICE: return AndroidMode.getTextString("menu.sketch.run_on_device");
    case RUN_IN_EMULATOR: return AndroidMode.getTextString("menu.sketch.run_in_emulator");
    case STOP:   return AndroidMode.getTextString("menu.sketch.stop");
    case NEW:    return AndroidMode.getTextString("menu.file.new");
    case OPEN:   return AndroidMode.getTextString("menu.file.open");
    case SAVE:   return AndroidMode.getTextString("menu.file.save");
    case EXPORT_PACKAGE:  return AndroidMode.getTextString("menu.file.export_signed_package");
    case EXPORT_BUNDLE:  return AndroidMode.getTextString("menu.file.export_signed_bundle");
    case EXPORT_PROJECT: return AndroidMode.getTextString("menu.file.export_android_project");
    }
    return null;
  }


  @Override
  public List<EditorButton> createButtons() {
    // aEditor not ready yet because this is called by super()
    final boolean debug = ((AndroidEditor) editor).isDebuggerEnabled();
    // final boolean debug = false;
    

    ArrayList<EditorButton> toReturn = new ArrayList<EditorButton>();
    final String runText = debug ?
            Language.text("toolbar.debug") : Language.text("Run on Device");
    runButton = new EditorButton(this,
                                 "/lib/toolbar/run",
                                        runText,
                                 "Run on emulator") {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleRun(e.getModifiers());
      }
    };
    toReturn.add(runButton);

    if (debug) {
      stepButton = new EditorButton(this,
              "/lib/toolbar/step",
              Language.text("menu.debug.step"),
              Language.text("menu.debug.step_into"),
              Language.text("menu.debug.step_out")) {
        @Override
        public void actionPerformed(ActionEvent e) {
          final int mask = ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK;
          handleStep(e.getModifiers() & mask);
        }
      };
      toReturn.add(stepButton);

      continueButton = new EditorButton(this,
              "/lib/toolbar/continue",
              Language.text("menu.debug.continue")) {
        @Override
        public void actionPerformed(ActionEvent e) {
          aEditor.getDebugger().continueDebug();
        }
      };
      toReturn.add(continueButton);
    }

    stopButton = new EditorButton(this,
                                  "/lib/toolbar/stop",
                                  Language.text("toolbar.stop")) {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleStop();
      }
    };
    toReturn.add(stopButton);

    return toReturn;
  }

  private void handleStep(int modifiers) {
    if (modifiers == 0) {
      aEditor.getDebugger().stepOver();
    } else if ((modifiers & ActionEvent.SHIFT_MASK) != 0) {
      aEditor.getDebugger().stepInto();
    } else if ((modifiers & ActionEvent.ALT_MASK) != 0) {
      aEditor.getDebugger().stepOut();
    }
  }  
  
  @Override
  public void addModeButtons(Box box, JLabel label) {

    EditorButton debugButton =
            new EditorButton(this, "/lib/toolbar/debug",
                    Language.text("toolbar.debug")) {
              @Override
              public void actionPerformed(ActionEvent e) {
                aEditor.toggleDebug();
              }
            };

    if (((AndroidEditor) editor).isDebuggerEnabled()) {
      debugButton.setSelected(true);
    }
//    debugButton.setRolloverLabel(label);
    box.add(debugButton);
    addGap(box);

  }

  @Override
  public void handleRun(int modifiers) {
    boolean shift = (modifiers & InputEvent.SHIFT_MASK) != 0;
    if (!shift) {
      aEditor.handleRunDevice();
    } else {
      aEditor.handleRunEmulator();
    }
  }


  @Override
  public void handleStop() {
    // TODO Auto-generated method stub
    aEditor.handleStop();
  }


  public void activateExport() {
    // TODO added to match the new API in EditorToolbar (activateRun, etc).
  }


  public void deactivateExport() {
    // TODO added to match the new API in EditorToolbar (activateRun, etc).
  }

  public void activateContinue() {
    continueButton.setSelected(true);
    repaint();
  }

  public void deactivateContinue() {
    continueButton.setSelected(false);
    repaint();
  }

  public void activateStep() {
    stepButton.setSelected(true);
    repaint();
  }

  public void deactivateStep() {
    stepButton.setSelected(false);
    repaint();
  }
}
