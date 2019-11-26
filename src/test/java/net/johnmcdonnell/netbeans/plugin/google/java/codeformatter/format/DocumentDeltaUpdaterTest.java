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

import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author John McDonnell
 */
public class DocumentDeltaUpdaterTest {
    
    @Test
    public void testHandlingOfInsertDelta() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(3,  Collections.singletonList("        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 1.5\");"));
        InsertDelta insertDelta = new InsertDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(insertDelta, documentToUpdate);
        
        //When
        instance.run();
        
        //Then
        assertTrue(!documentToUpdate.equals(getTestDocument()));
        assertTrue(documentToUpdate.getLength() > initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount + 1);
    }
    
    @Test
    public void testHandlingOfDeleteDelta() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(3,  Collections.singletonList("        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 1.5\");"));
        DeleteDelta deleteDelta = new DeleteDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(deleteDelta, documentToUpdate);
        
        //When
        instance.run();
        
        //Then
        assertTrue(!documentToUpdate.equals(getTestDocument()));
        assertTrue(documentToUpdate.getLength() < initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount - 1);
    }
    
    @Test
    public void testHandlingOfChangeDelta() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(3,  Collections.singletonList("        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 3\");"));
        ChangeDelta changeDelta = new ChangeDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(changeDelta, documentToUpdate);
        
        //When
        instance.run();
        
        //Then
        assertTrue(!documentToUpdate.equals(getTestDocument()));
        assertTrue(documentToUpdate.getLength() == initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount);
    }

    @Test
    public void testHandlingOfDeleteDeltaOfMultipleLines() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(2,  Arrays.asList("        System.out.println(\"Test Line 1\");","        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 1.5\");"));
        DeleteDelta deleteDelta = new DeleteDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(deleteDelta, documentToUpdate);
        
        //When
        instance.run();
        
        //Then
        assertTrue(!documentToUpdate.equals(getTestDocument()));
        assertTrue(documentToUpdate.getLength() < initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount - 2);
    }
    
    private StyledDocument getTestDocument() throws BadLocationException {
        StyledDocument document = new DefaultStyledDocument();
        document.insertString(0, "public class TestClass {\n" +
"    public static void main(String[] args) {\n" +
"        System.out.println(\"Test Line 1\");\n" +
"        System.out.println(\"Test Line 2\");\n" +
"    }\n" +
"}", null);
        return document;
    }
    
}
