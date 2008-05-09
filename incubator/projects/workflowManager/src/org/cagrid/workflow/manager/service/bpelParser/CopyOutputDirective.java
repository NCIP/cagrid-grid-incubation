package org.cagrid.workflow.manager.service.bpelParser;

// It stores the data related to the copy tags of the assign
	class CopyOutputDirective{
		private String fromVariable;
		private String fromPart;
		private String query;
		private String queryLanguage;
		private String toVariable;
		private String toPart;

		public void setFromVariable(String fromVariable){
			this.fromVariable = fromVariable;
		}
		public void setFromPart(String fromPart){
			this.fromPart = fromPart;
		}
		public void setToVariable(String toVariable){
			this.toVariable = toVariable;
		}
		public void setToPart(String toPart){
			this.toPart = toPart;
		}
		public String getFromVariable(){
			return fromVariable;	
		}
		public String getFromPart(){
			return fromPart;	
		}
		public String getToVariable(){
			return toVariable;	
		}
		public String getToPart(){
			return toPart;	
		}
		void printClass(){
			System.out.println("  fromVariable = "+ fromVariable);
			System.out.println("  fromPart = "+ fromPart);
			System.out.println("  toVariable = "+ toVariable);
			System.out.println("  toPart = "+toPart);
		}
		/**
		 * @return the query
		 */
		public String getQuery() {
			return query;
		}
		/**
		 * @param query the query to set
		 */
		public void setQuery(String query) {
			this.query = query;
		}
		/**
		 * @return the queryLanguage
		 */
		public String getQueryLanguage() {
			return queryLanguage;
		}
		/**
		 * @param queryLanguage the queryLanguage to set
		 */
		public void setQueryLanguage(String queryLanguage) {
			this.queryLanguage = queryLanguage;
		}
	}