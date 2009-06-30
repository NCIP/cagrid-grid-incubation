package org.cagrid.identifiers.resolver;

public class ResolverFactory {
	public static Resolver getResolver() {
		return new CQLResolver();
	}
}
