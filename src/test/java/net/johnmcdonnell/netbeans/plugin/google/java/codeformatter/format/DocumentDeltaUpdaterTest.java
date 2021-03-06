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
        Chunk originalChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 1.5\");"));
        InsertDelta insertDelta = new InsertDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getBasicTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();

        //When
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(Collections.singletonList(insertDelta), documentToUpdate);
        instance.run();

        //Then
        assertTrue(!documentToUpdate.equals(getBasicTestDocument()));
        assertTrue(documentToUpdate.getLength() > initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount + 1);
    }

    @Test
    public void testHandlingOfDeleteDelta() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 1.5\");"));
        DeleteDelta deleteDelta = new DeleteDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getBasicTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();

        //When
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(Collections.singletonList(deleteDelta), documentToUpdate);
        instance.run();

        //Then
        assertTrue(!documentToUpdate.equals(getBasicTestDocument()));
        assertTrue(documentToUpdate.getLength() < initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount - 1);
    }

    @Test
    public void testHandlingOfChangeDelta() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(3, Collections.singletonList("        System.out.println(\"Test Line 3\");"));
        ChangeDelta changeDelta = new ChangeDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getBasicTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();

        //When
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(Collections.singletonList(changeDelta), documentToUpdate);
        instance.run();

        //Then
        assertTrue(!documentToUpdate.equals(getBasicTestDocument()));
        assertTrue(documentToUpdate.getLength() == initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount);
    }

    @Test
    public void testHandlingOfDeleteDeltaOfMultipleLines() throws BadLocationException {
        // Given
        Chunk originalChunk = new Chunk(2, Arrays.asList("        System.out.println(\"Test Line 1\");", "        System.out.println(\"Test Line 2\");"));
        Chunk revisedChunk = new Chunk(2, Collections.singletonList("        System.out.println(\"Test Line 1.5\");"));
        DeleteDelta deleteDelta = new DeleteDelta(originalChunk, revisedChunk);
        StyledDocument documentToUpdate = getBasicTestDocument();
        int initialLength = documentToUpdate.getLength();
        int initialRowCount = documentToUpdate.getDefaultRootElement().getElementCount();

        //When
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(Collections.singletonList(deleteDelta), documentToUpdate);
        instance.run();

        //Then
        assertTrue(!documentToUpdate.equals(getBasicTestDocument()));
        assertTrue(documentToUpdate.getLength() < initialLength);
        assertTrue(documentToUpdate.getDefaultRootElement().getElementCount() == initialRowCount - 2);
    }

    @Test
    public void testHandlingOfMultipleDiffsCorrectly() throws BadLocationException {
        // Given
        Chunk originalChunk1 = new Chunk(2, Arrays.asList("/** @author john", "", "", "", "", "", "", "*/"));
        Chunk revisedChunk1 = new Chunk(2, Collections.singletonList("/** @author john */"));
        ChangeDelta changeDelta1 = new ChangeDelta(originalChunk1, revisedChunk1);
        
        Chunk originalChunk2 = new Chunk(12, Arrays.asList("    public void test() ", "    {", "        Number num", "                ;", "    }"));
        Chunk revisedChunk2 = new Chunk(5, Arrays.asList("  public void test() {", "    Number num;", "  }"));
        ChangeDelta changeDelta2 = new ChangeDelta(originalChunk2, revisedChunk2);
        
        StyledDocument documentToUpdate = getMultipleIssueDocument();

        //When
        DocumentDeltaUpdater instance = new DocumentDeltaUpdater(Arrays.asList(changeDelta1, changeDelta2), documentToUpdate);
        instance.run();
        
        //Then
        assertTrue(!documentToUpdate.equals(getMultipleIssueDocument()));
        assertTrue(getMultipleIssueSolution().equals(documentToUpdate.getText(0, documentToUpdate.getLength())));
    }
    
    private StyledDocument getBasicTestDocument() throws BadLocationException {
        StyledDocument document = new DefaultStyledDocument();
        document.insertString(0, "public class TestClass {\n"
                + "    public static void main(String[] args) {\n"
                + "        System.out.println(\"Test Line 1\");\n"
                + "        System.out.println(\"Test Line 2\");\n"
                + "    }\n"
                + "}", null);
        return document;
    }

    private StyledDocument getMultipleIssueDocument() throws BadLocationException {
        StyledDocument document = new DefaultStyledDocument();
        document.insertString(0, "package test;\n"
                + "\n"
                + "/** @author john\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "*/\n"
                + "public class TestClass {\n"
                + "\n"
                + "    public void test() \n"
                + "    {\n"
                + "        Number num\n"
                + "                ;\n"
                + "    }\n"
                + "}", null);
        return document;
    }

    private String getMultipleIssueSolution() {
        return "package test;\n"
                + "\n"
                + "/** @author john */\n"
                + "public class TestClass {\n"
                + "\n"
                + "  public void test() {\n"
                + "    Number num;\n"
                + "  }\n"
                + "}";
    }
}
