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

package net.johnmcdonnell.netbeans.plugin.google.java.codeformatter.ui;

import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;

/** @author John McDonnell */
@ProjectCustomizer.CompositeCategoryProvider.Registrations({
  @ProjectCustomizer.CompositeCategoryProvider.Registration(
      projectType = "org-netbeans-modules-maven",
      category = "Formatting",
      position = 90),
  @ProjectCustomizer.CompositeCategoryProvider.Registration(
      projectType = "org-netbeans-modules-gradle",
      category = "Formatting",
      position = 90),
})
public class GoogleJavaFormatterProjectCustomizer
    implements ProjectCustomizer.CompositeCategoryProvider {

  @Override
  public ProjectCustomizer.Category createCategory(Lookup look) {
    return ProjectCustomizer.Category.create(
        "google-java-code-formatter", "Google Java Style", null);
  }

  @Override
  public JComponent createComponent(ProjectCustomizer.Category cat, Lookup lookup) {
    Project project = lookup.lookup(Project.class);
    return new GoogleJavaFormatterPanel(cat, project);
  }
}
