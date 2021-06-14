/*
   Copyright 2021 KingCreator11

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.kingcreator11.simplesorter;

/**
 * Base class of the plugin, all other classes within the plugin extend this
 */
public abstract class SimpleSorterBase {
	
	/**
	 * Pointer to the plugin
	 */
	protected SimpleSorter plugin;

	/**
	 * Creates a new base class with a reference to the plugin
	 * @param plugin Plugin reference
	 */
	public SimpleSorterBase(SimpleSorter plugin) {
		this.plugin = plugin;
	}
}