import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    public boolean canReachCorner(int xCorner, int yCorner, int[][] circles) {
        var question = new Question(
                new Area(xCorner, yCorner),
                Arrays.stream(circles)
                        .map(c -> new Circle(c[0], c[1], c[2]))
                        .collect(Collectors.toList()));

        return explore(question) == null;
    }

    public static Path explore(Question question) {
        var startPaths = new ArrayList<Path>();
        var endPaths = new ArrayList<Path>();
        var currentCircles = new LinkedList<Circle>();

        for (var circle : question.circles) {
            if (pointCovering(0, 0, circle) ||
                    pointCovering(question.area.width, question.area.height, circle)) {
                return new Path(new ArrayList<>(Arrays.asList(circle)));
            }

            var start = intersectingY(circle, 0, question.area) ||
                    intersectingX(circle, question.area.width, question.area);
            if (start) {
                startPaths.add(new Path(new ArrayList<>(Arrays.asList(circle))));
            }

            var end = intersectingY(circle, question.area.height, question.area) ||
                    intersectingX(circle, 0, question.area);
            if (end) {
                endPaths.add(new Path(new ArrayList<>(Arrays.asList(circle))));
            }

            if (start && end) {
                return new Path(Arrays.asList(circle));
            }

            if (!start && !end) {
                currentCircles.add(circle);
            }
        }

        for (var startPath : startPaths) {
            var startCircle = startPath.circles.get(startPath.circles.size() - 1);
            for (var endPath : endPaths) {
                var endCircle = endPath.circles.get(endPath.circles.size() - 1);
                if (intersectingCircle(startCircle, endCircle, question.area)) {
                    return new Path(Arrays.asList(startCircle, endCircle));
                }
            }
        }

        Circle currentCircle;
        while ((currentCircle = currentCircles.poll()) != null) {
            Path targetPath = null;

            for (Path startPath : startPaths) {
                var pathCircle = startPath.circles.get(startPath.circles.size() - 1);
                if (intersectingCircle(currentCircle, pathCircle, question.area)) {
                    startPath.circles.add(currentCircle);
                    targetPath = startPath;
                }
            }

            for (Path endPath : endPaths) {
                var pathCircle = endPath.circles.get(endPath.circles.size() - 1);
                if (intersectingCircle(currentCircle, pathCircle, question.area)) {
                    if (targetPath != null) {
                        var output = new ArrayList<>(targetPath.circles);

                        var end = new ArrayList<>(endPath.circles);
                        Collections.reverse(end);

                        output.addAll(end);
                        return new Path(output);
                    }

                    endPath.circles.add(currentCircle);
                }
            }
        }

        return null;
    }

    public static boolean intersectingCircle(Circle circle1, Circle circle2, Area area) {
        long dx = circle1.x - circle2.x;
        long dy = circle1.y - circle2.y;
        long radiusSum = circle1.radius + circle2.radius;

        if (dx * dx + dy * dy > radiusSum * radiusSum) {
            return false;
        }

        double midpointX = ((long) circle1.x * circle2.radius + (long) circle2.x * circle1.radius) / (double) radiusSum;
        double midpointY = ((long) circle1.y * circle2.radius + (long) circle2.y * circle1.radius) / (double) radiusSum;

        return midpointX >= 0 && midpointX <= area.width &&
                midpointY >= 0 && midpointY <= area.height;
    }

    public static boolean intersectingX(Circle circle, int x, Area area) {
        int dx = circle.x - x;
        if (Math.abs(dx) <= circle.radius) {
            double xy = Math.sqrt((long) circle.radius * circle.radius - (long) dx * dx);
            double y1 = circle.y - xy;
            double y2 = circle.y + xy;

            return (0 < y1 && y1 < area.height) || (0 < y2 && y2 < area.height);
        }

        return false;
    }

    public static boolean intersectingY(Circle circle, int y, Area area) {
        int dy = circle.y - y;
        if (Math.abs(dy) <= circle.radius) {
            double yx = Math.sqrt((long) circle.radius * circle.radius - (long) dy * dy);
            double x1 = circle.x - yx;
            double x2 = circle.x + yx;

            return (0 < x1 && x1 < area.width) || (0 < x2 && x2 < area.width);
        }

        return false;
    }

    public static boolean pointCovering(int x, int y, Circle circle) {
        long dx = x - circle.x;
        long dy = y - circle.y;

        return dx * dx + dy * dy <= (long) circle.radius * circle.radius;
    }
}
