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
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author John McDonnell
 */
public class DocumentDeltaUpdater implements Runnable {

    private final AbstractDelta<String> delta;
    private final StyledDocument document;

    public DocumentDeltaUpdater(AbstractDelta<String> delta, StyledDocument document) {
        this.delta = delta;
        this.document = document;
    }

    @Override
    public void run() {
        try {
            if (null != delta.getType()) {
                switch (delta.getType()) {
                    case DELETE:
                        removeLinesFromDocument();
                        break;
                    case INSERT:
                        insertLinesToDocument();
                        break;
                    case CHANGE:
                        removeLinesFromDocument();
                        insertLinesToDocument();
                        break;
                    default:
                        // Only other option is EQUALS and we have nothing to do
                        break;
                }
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void insertLinesToDocument() throws BadLocationException {
        int findLineOffset = NbDocument.findLineOffset(document, delta.getTarget().getPosition());

        for (int idx = 0; idx < delta.getTarget().getLines().size(); idx++) {
            document.insertString(findLineOffset + idx, delta.getTarget().getLines().get(idx) + "\n", null);
        }
    }

    private void removeLinesFromDocument() throws BadLocationException {
        delta.getSource().getLines().forEach((t) -> {
            try {
                Element rootLineElement = NbDocument.findLineRootElement(document);
                Element element = rootLineElement.getElement(delta.getSource().getPosition());
                document.remove(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }
}
