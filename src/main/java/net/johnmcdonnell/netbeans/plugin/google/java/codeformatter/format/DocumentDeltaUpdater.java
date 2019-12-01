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
package net.johnmcdonnell.netbeans.plugin.google.java.codeformatter.format;

import com.github.difflib.patch.AbstractDelta;
import java.util.List;
import java.util.Objects;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/** @author John McDonnell */
public class DocumentDeltaUpdater implements Runnable {

  private final List<AbstractDelta<String>> deltas;
  private final StyledDocument document;

  public DocumentDeltaUpdater(List<AbstractDelta<String>> deltas, StyledDocument document) {
    this.deltas = Objects.requireNonNull(deltas);
    this.document = Objects.requireNonNull(document);
  }

  @Override
  public void run() {
    for (AbstractDelta<String> delta : deltas) {
      try {
        switch (delta.getType()) {
          case DELETE:
            removeLinesFromDocument(delta);
            break;
          case INSERT:
            insertLinesToDocument(delta);
            break;
          case CHANGE:
            removeLinesFromDocument(delta);
            insertLinesToDocument(delta);
            break;
          default:
            // Only other option is EQUALS and we have nothing to do
            break;
        }
      } catch (BadLocationException ex) {
        Exceptions.printStackTrace(ex);
      }
    }
  }

  private void insertLinesToDocument(AbstractDelta<String> delta) throws BadLocationException {
    int position = delta.getTarget().getPosition();
    int findLineOffset = NbDocument.findLineOffset(document, position);

    StringBuilder contentToAdd = new StringBuilder();
    delta
        .getTarget()
        .getLines()
        .forEach(
            (String line) -> {
              contentToAdd.append(line).append("\n");
            });
    document.insertString(findLineOffset, contentToAdd.toString(), null);
  }

  private void removeLinesFromDocument(AbstractDelta<String> delta) throws BadLocationException {
    int startOfChangeLineNumber = delta.getTarget().getPosition();
    int offset = 0;

    for (String line : delta.getSource().getLines()) {
      offset += line.length() + "\n".length();
    }

    try {
      Element rootLineElement = NbDocument.findLineRootElement(document);
      Element element = rootLineElement.getElement(startOfChangeLineNumber);
      document.remove(element.getStartOffset(), offset);
    } catch (BadLocationException ex) {
      Exceptions.printStackTrace(ex);
    }
  }
}
