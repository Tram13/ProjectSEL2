package be.sel2.api.util_tests;

import be.sel2.api.util.specifications.OffsetBasedPageable;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class PageableTest {

    @Test
    void defaultIsUnsorted() {
        Pageable pageable = new OffsetBasedPageable(0, 50);

        assertTrue(pageable.getSort().isUnsorted());
    }

    @Test
    void pageSizeShouldBeLimit() {
        int[] testedLimits = {2, 5, 50, 78, 100};

        for (int limit : testedLimits) {
            Pageable pageable = new OffsetBasedPageable(0, limit);

            assertEquals(limit, pageable.getPageSize());
        }
    }

    @Test
    void offsetShouldBeCorrect() {
        int[] testedOffsets = {0, 5, 36, 100};

        for (int offset : testedOffsets) {
            Pageable pageable = new OffsetBasedPageable(offset, 15);

            assertEquals(offset, pageable.getOffset());
        }
    }

    @Test
    void nextAndPreviousShouldShiftByLimit() {
        int[] testedLimits = {2, 5, 50, 78, 100};
        int baseOffset = 114; //Must be greater than max(testedLimits)

        for (int limit : testedLimits) {
            Pageable pageable = new OffsetBasedPageable(baseOffset, limit);

            assertEquals(baseOffset, pageable.getOffset());
            assertEquals(baseOffset + limit, pageable.next().getOffset());
            assertEquals(baseOffset - limit, pageable.previousOrFirst().getOffset());
        }
    }

    @Test
    void firstShouldStartAtZero() {
        int[] testedLimits = {2, 5, 50, 78, 100};
        int baseOffset = 114; //Must be greater than max(testedLimits)

        for (int limit : testedLimits) {
            Pageable pageable = new OffsetBasedPageable(baseOffset, limit);

            assertEquals(0, pageable.first().getOffset());
        }
    }

    @Test
    void illegalArgumentsTesting() {
        assertThrows(IllegalArgumentException.class, () -> new OffsetBasedPageable(-1, 20));
        assertThrows(IllegalArgumentException.class, () -> new OffsetBasedPageable(0, 0));
    }

    @Test
    void settersTest() {
        int[] testedLimits = {2, 5, 50, 78, 100};
        int[] testedOffsets = {0, 5, 36, 100};

        OffsetBasedPageable pageable = new OffsetBasedPageable(0, 1);

        for (int limit : testedLimits) {
            pageable.setLimit(limit);

            assertEquals(limit, pageable.getPageSize());
        }

        for (int offset : testedOffsets) {
            pageable.setOffset(offset);

            assertEquals(offset, pageable.getOffset());
        }
    }

    @Test
    void testMundaneInformation() {
        int[] testedLimits = {2, 5, 50, 78};
        int[] testedOffsets = {0, 5, 36, 100};

        for (int i = 0; i < 4; i++) {
            int limit = testedLimits[i];
            int offset = testedOffsets[i];

            OffsetBasedPageable pageable = new OffsetBasedPageable(offset, limit);

            assertEquals(offset > limit, pageable.hasPrevious());
            assertEquals(offset / limit, pageable.getPageNumber());

            if (pageable.hasPrevious()) {
                assertEquals(pageable.previousOrFirst(), pageable.previous());
            } else {
                assertEquals(pageable.previousOrFirst(), pageable.first());
                assertEquals(pageable.previous(), pageable);
            }

            assertEquals(new OffsetBasedPageable(offset, limit), pageable);
            assertNotEquals(new OffsetBasedPageable(offset, limit + 10), pageable);
            assertNotEquals(pageable.next(), pageable);
            assertNotEquals(
                    new OffsetBasedPageable(
                            offset, limit,
                            Sort.by(Sort.Direction.DESC, "id")
                    ),
                    pageable
            );

            String stringify = pageable.toString();

            assertTrue(stringify.startsWith("OffsetBasedPageable"));
            assertTrue(stringify.contains("limit=" + limit));
            assertTrue(stringify.contains("offset=" + offset));
            assertTrue(stringify.contains("sort=" + Sort.unsorted()));

            assertEquals(new OffsetBasedPageable(offset, limit).hashCode(), pageable.hashCode());
            assertNotEquals(new OffsetBasedPageable(offset, limit + 1).hashCode(), pageable.hashCode());
        }
    }
}
