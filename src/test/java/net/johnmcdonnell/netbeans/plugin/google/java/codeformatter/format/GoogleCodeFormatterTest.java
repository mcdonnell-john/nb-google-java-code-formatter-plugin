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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openide.util.Exceptions;
 
/**
 *
 * @author John McDonnell
 */
public class GoogleCodeFormatterTest {


    @Test
    public void testFormattingOfFullJavaClass() throws BadLocationException {
        // Given
        StyledDocument originalClass = getTestClassAsDocument("OriginalClass.txt");
        
        // When
        GoogleCodeFormatter formatter = new GoogleCodeFormatter();
        formatter.format(originalClass);
        
        // Then
        StyledDocument expectedClass = getTestClassAsDocument("ExpectedClass.txt");
        
        assertTrue(expectedClass.getText(0, expectedClass.getLength()).equals(originalClass.getText(0, originalClass.getLength()))); 
    }

    private StyledDocument getTestClassAsDocument(String fileName) throws BadLocationException {
        StyledDocument document = new DefaultStyledDocument();
        document.insertString(0, readFileAsString(fileName), null);
        
        return document;
    }

    private String readFileAsString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {

            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            stringBuilder.append(result);

        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return stringBuilder.toString();
    }
}