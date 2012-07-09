package raisa.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {
	public static <T> List<T> takeLast(List<T> list, int length) {
		if (list.size() > length) {
			int fromIndex = Math.max(0, list.size() - length);
			int toIndex = list.size();
			List<T> newList = new ArrayList<T>();
			newList.addAll(list.subList(fromIndex, toIndex));
			return newList;
		}
		return list;
	}	
}
