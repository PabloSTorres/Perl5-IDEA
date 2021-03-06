/*
 * Copyright 2015 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.idea.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.perl5.lang.perl.psi.PerlMethod;
import com.perl5.lang.perl.psi.PerlNamespaceElement;
import com.perl5.lang.perl.psi.PerlSubNameElement;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.references.PerlSubReference;
import org.jetbrains.annotations.NotNull;

/**
 * Created by hurricup on 14.06.2015.
 */
public class PerlSubUnresolvableInspection extends PerlInspection
{
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly)
	{
		return new PerlVisitor()
		{
			@Override
			public void visitPerlMethod(@NotNull PerlMethod o)
			{
				PerlNamespaceElement namespaceElement = o.getNamespaceElement();
				PerlSubNameElement subNameElement = o.getSubNameElement();

				boolean hasExplicitNamespace = namespaceElement != null && !"CORE".equals(namespaceElement.getCanonicalName());

				// fixme adjust built in checking to the file; Remove second condition after implementing annotations
				if (subNameElement == null || (namespaceElement != null && namespaceElement.isBuiltin()) || (!hasExplicitNamespace && subNameElement.isBuiltIn()))
					return;

				PsiReference reference = subNameElement.getReference();

				if (reference instanceof PerlSubReference && ((PerlSubReference) reference).multiResolve(false).length == 0)
					registerProblem(holder, subNameElement, "Unable to find sub definition, declaration, constant definition or typeglob aliasing");
			}
		};
	}
}
