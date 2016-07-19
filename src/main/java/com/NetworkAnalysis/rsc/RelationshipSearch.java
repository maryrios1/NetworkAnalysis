package com.NetworkAnalysis.rsc;

public enum RelationshipSearch {
	RETWEETED{
		@Override
		public String toString() {
			return "RETWEETED";
		}
	},
	REPLIED{
		@Override
		public String toString() {
			return "REPLIED";
		}
	},
	MENTIONED{
		@Override
		public String toString() {
			return "MENTIONED";
		}
	},
	CONTRIBUTOR	{
		@Override
		public String toString() {
			return "CONTRIBUTOR";
		}
	}
}
