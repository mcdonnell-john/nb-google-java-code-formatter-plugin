/*
 * Copyright 2019 John McDonnell.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.johnmcdonnell.netbeans.plugin.google.java.codeformatter.task;

import javax.swing.text.StyledDocument;
import net.johnmcdonnell.netbeans.plugin.google.java.codeformatter.format.GoogleCodeFormatter;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.netbeans.spi.editor.document.OnSaveTask.Context;
import org.netbeans.spi.editor.document.OnSaveTask.Factory;
import org.netbeans.spi.project.AuxiliaryProperties;

/** @author John McDonnell */
public class FormatOnSaveTask implements OnSaveTask {

  private final Context context;
  private final Boolean isCodeFormatterEnabled;

  private FormatOnSaveTask(Context context, Boolean isCodeFormatterEnabled) {
    this.context = context;
    this.isCodeFormatterEnabled = isCodeFormatterEnabled;
  }

  @Override
  public void performTask() {
    if (isCodeFormatterEnabled) {
      GoogleCodeFormatter googleCodeFormatter = new GoogleCodeFormatter();
      googleCodeFormatter.format((StyledDocument) context.getDocument());
    }
  }

  @Override
  public void runLocked(Runnable run) {
    run.run();
  }

  @Override
  public boolean cancel() {
    return true;
  }

  @MimeRegistration(mimeType = "text/x-java", service = OnSaveTask.Factory.class, position = 1500)
  public static final class FactoryImpl implements Factory {

    @Override
    public OnSaveTask createTask(Context context) {
      final StyledDocument styledDoc = (StyledDocument) context.getDocument();
      Project project =
          FileOwnerQuery.getOwner(NbEditorUtilities.getDataObject(styledDoc).getPrimaryFile());
      AuxiliaryProperties auxiliaryProperties =
          project.getLookup().lookup(AuxiliaryProperties.class);
      return new FormatOnSaveTask(
          context,
          Boolean.valueOf(auxiliaryProperties.get("isGoogleJavaCodeFormatterEnabled", true)));
    }
  }
}
