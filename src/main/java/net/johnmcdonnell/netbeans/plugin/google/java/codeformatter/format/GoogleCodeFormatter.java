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

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.AbstractDelta;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import static java.util.Comparator.comparing;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author John McDonnell
 */
public class GoogleCodeFormatter {

    public void format(StyledDocument document) {
        Formatter formatter = new Formatter(JavaFormatterOptions.defaultOptions());
        try {
            final String existingText = document.getText(0, document.getLength());

            final String formatSourceAndFixImports = formatter.formatSourceAndFixImports(existingText);

            if (formatSourceAndFixImports != null && !formatSourceAndFixImports.equals(existingText)) {
                try {
                    List<AbstractDelta<String>> deltas = DiffUtils.diff(existingText, formatSourceAndFixImports, null).getDeltas();
                    for (AbstractDelta<String> delta : sortDeltas(deltas)) {
                        NbDocument.runAtomicAsUser(document, new DocumentDeltaUpdater(delta, document));
                    }
                } catch (DiffException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (FormatterException | BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private List<AbstractDelta<String>> sortDeltas(List<AbstractDelta<String>> deltas) {
        deltas.sort(comparing(d -> d.getSource().getPosition()));
        return deltas;
    }
}
