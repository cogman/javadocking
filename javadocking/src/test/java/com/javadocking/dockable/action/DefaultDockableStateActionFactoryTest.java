package com.javadocking.dockable.action;

import com.javadocking.dockable.DockableState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultDockableStateActionFactoryTest {

	@Test
	void getIcon() {
		final int[] allStates = DockableState.statesAll();
		final Stream<Executable> iconConversions = Arrays.stream(allStates)
				.mapToObj((s) -> Stream.<Executable>of(
						() -> DefaultDockableStateActionFactory.getIcon(s, true),
						() -> DefaultDockableStateActionFactory.getIcon(s, false))
				)
				.flatMap(Function.identity());

		assertAll(iconConversions);
	}
}