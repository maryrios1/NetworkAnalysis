package com.NetworkAnalysis.rsc;

public enum SearchType {
	SEARCH {
		@Override
		public String toString() {
			return "SEARCH";
		}
	},
	STREAM {
		@Override
		public String toString() {
			return "STREAM";
		}
	}
}
