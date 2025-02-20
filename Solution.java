import java.util.*;
import java.util.stream.*;

public class Solution {
    public boolean canReachCorner(int xCorner, int yCorner, int[][] circles) {
        Question question = new Question(
                new Area(xCorner, yCorner),
                Arrays.stream(circles)
                        .map(c -> new Circle(c[0], c[1], c[2]))
                        .toArray(Circle[]::new));

        return !covering(question)
                && explore(question);
    }

    private static boolean covering(Question question) {
        for (var c : question.circles) {
            if ((long) c.x * c.x + (long) c.y * c.y <= (long) c.radius * c.radius) {
                return true;
            }

            if (((long) c.x - question.area.width) * ((long) c.x - question.area.width)
                    + ((long) c.y - question.area.height)
                            * ((long) c.y - question.area.height) <= (long) c.radius * c.radius) {
                return true;
            }
        }

        return false;
    }

    private static boolean explore(Question question) {
        var steps = new ArrayList<>();
        IStep currentStep = new RightLineStep(0);

        for (int i = 0; i < question.circles.length * 3 + 50; i++) {
            var nextStep = moveStep(currentStep, question);

            if (nextStep == null)
                return false;
            if (nextStep instanceof CornerEndStep)
                return true;
            if (nextStep instanceof LeftEndStep
                    || nextStep instanceof BottomEndStep)
                return false;

            steps.add(currentStep);
            currentStep = nextStep;
        }

        return false;
    }

    private static IStep moveStep(IStep currentStep, Question question) {
        if (currentStep instanceof RightLineStep) {
            return moveRight((RightLineStep) currentStep, question);
        } else if (currentStep instanceof DownLineStep) {
            return moveDown((DownLineStep) currentStep, question);
        } else if (currentStep instanceof ArcStep) {
            return moveArc((ArcStep) currentStep, question);
        } else {
            throw new IllegalStateException();
        }
    }

    private static IStep moveRight(RightLineStep currentStep, Question question) {
        ArcStep arcStep = Arrays.stream(question.circles)
                .flatMap(circle -> {
                    long delta = (long) circle.radius * circle.radius - (long) circle.y * circle.y;
                    if (delta < 0)
                        return Stream.empty();

                    double dx = Math.sqrt(delta);
                    double[] xs = { circle.x - dx, circle.x + dx };

                    return Arrays.stream(xs).mapToObj(x -> new ArcStep(x, 0, circle));
                })
                .filter(nextStep -> nextStep.x > currentStep.x)
                .filter(nextStep -> nextStep.x < question.area.width)
                .sorted(Comparator.comparingDouble(a -> a.x))
                .findFirst()
                .orElse(null);

        if (arcStep != null) {
            return arcStep;
        }

        return new DownLineStep(0);
    }

    private static IStep moveDown(DownLineStep currentStep, Question question) {
        var arcStep = Arrays.stream(question.circles)
                .flatMap(circle -> {
                    long delta = (long) circle.radius * circle.radius
                            - (long) (question.area.width - circle.x) * (question.area.width - circle.x);

                    if (delta < 0)
                        return Stream.empty();

                    double dy = Math.sqrt(delta);
                    double[] ys = { circle.y - dy, circle.y + dy };

                    return Arrays.stream(ys).mapToObj(y -> new ArcStep(question.area.width, y, circle));
                })
                .filter(nextStep -> nextStep.y > currentStep.y)
                .filter(nextStep -> nextStep.y < question.area.height)
                .sorted(Comparator.comparingDouble(nextStep -> nextStep.y))
                .findFirst();

        if (arcStep.isPresent()) {
            return arcStep.get();
        }

        return new CornerEndStep();
    }

    private static IStep moveArc(ArcStep currentStep, Question question) {
        double currentAngle = Math.atan2(currentStep.y - currentStep.circle.y, currentStep.x - currentStep.circle.x);
        while (currentAngle >= Math.PI * 2) {
            currentAngle -= Math.PI * 2;
        }
        while (currentAngle < 0) {
            currentAngle += Math.PI * 2;
        }

        final var fromAngle = currentAngle;

        var nexts = Stream.of(
                getRightNexts(currentStep, question),
                getDownNexts(currentStep, question),
                getLeftNexts(currentStep, question),
                getBottomNexts(currentStep, question),
                getArcNexts(currentStep, question))
                .reduce(Stream::concat)
                .orElse(Stream.empty())
                .map(next -> {
                    double angle = next.angle;
                    while (angle >= Math.PI * 2) {
                        angle -= Math.PI * 2;
                    }
                    while (angle < 0) {
                        angle += Math.PI * 2;
                    }

                    angle -= fromAngle;
                    while (angle >= Math.PI * 2) {
                        angle -= Math.PI * 2;
                    }
                    while (angle < -0.000001) {
                        angle += Math.PI * 2;
                    }

                    angle += new Random().nextDouble() * 0.000001;

                    return new ArcNext(angle, next.step);
                })
                .sorted(Comparator.comparingDouble(next -> next.angle))
                .map(next -> next.step)
                .toArray(IStep[]::new);

        if (nexts.length <= 0) {
            return null;
        }

        return nexts[nexts.length - 1];
    }

    private static Stream<ArcNext> getRightNexts(ArcStep currentStep, Question question) {
        return getXIntersects(currentStep, 0, question)
                .map(xIntersect -> new ArcNext(xIntersect.angle, new RightLineStep(xIntersect.x)));
    }

    private static Stream<ArcXIntersect> getXIntersects(ArcStep currentStep, int y, Question question) {
        long delta = (long) currentStep.circle.radius * currentStep.circle.radius
                - (long) (y - currentStep.circle.y) * (y - currentStep.circle.y);

        if (delta < 0) {
            return Stream.empty();
        }

        double dx = Math.sqrt(delta);
        double[] xs = { currentStep.circle.x + dx, currentStep.circle.x - dx };

        return Arrays.stream(xs)
                .filter(x -> x >= 0)
                .filter(x -> x <= question.area.width)
                .mapToObj(x -> new ArcXIntersect(x, Math.atan2(y - currentStep.circle.y, x - currentStep.circle.x)));
    }

    private static Stream<ArcNext> getDownNexts(ArcStep currentStep, Question question) {
        return getYIntersects(currentStep, question.area.width, question)
                .map(yIntersect -> new ArcNext(yIntersect.angle, new DownLineStep(yIntersect.y)));
    }

    private static Stream<ArcYIntersect> getYIntersects(ArcStep currentStep, int x, Question question) {
        long delta = (long) currentStep.circle.radius * currentStep.circle.radius
                - (long) (x - currentStep.circle.x) * (x - currentStep.circle.x);

        if (delta < 0) {
            return Stream.empty();
        }

        double dy = Math.sqrt(delta);
        double[] ys = { currentStep.circle.y + dy, currentStep.circle.y - dy };

        return Arrays.stream(ys)
                .filter(y -> y >= 0)
                .filter(y -> y <= question.area.height)
                .mapToObj(y -> new ArcYIntersect(y, Math.atan2(y - currentStep.circle.y, x - currentStep.circle.x)));
    }

    private static Stream<ArcNext> getLeftNexts(ArcStep currentStep, Question question) {
        return getYIntersects(currentStep, 0, question)
                .map(intersect -> new ArcNext(intersect.angle, new LeftEndStep(intersect.y)));
    }

    private static Stream<ArcNext> getBottomNexts(ArcStep currentStep, Question question) {
        return getXIntersects(currentStep, question.area.height, question)
                .map(intersect -> new ArcNext(intersect.angle, new BottomEndStep(intersect.x)));
    }

    private static Stream<ArcNext> getArcNexts(ArcStep currentStep, Question question) {
        Circle[] circles = Arrays.stream(question.circles)
                .filter(circle -> !circle.equals(currentStep.circle))
                .toArray(Circle[]::new);

        return Arrays.stream(circles)
                .flatMap(circle -> getCircleIntersects(currentStep.circle, circle)
                        .filter(intersect -> intersect.x > 0 && intersect.x < question.area.width
                                && intersect.y > 0 && intersect.y < question.area.height)
                        .map(intersect -> new ArcNext(
                                Math.atan2(intersect.y - currentStep.circle.y, intersect.x - currentStep.circle.x),
                                new ArcStep(intersect.x, intersect.y, circle))));
    }

    private static Stream<CircleIntersect> getCircleIntersects(Circle a, Circle b) {
        long dx = b.x - a.x;
        long dy = b.y - a.y;
        var d = Math.sqrt(dx * dx + dy * dy);

        if (d > a.radius + b.radius || d < Math.abs(a.radius - b.radius)) {
            return Stream.empty();
        }

        var a2 = ((long) a.radius * a.radius - (long) b.radius * b.radius + d * d) / (2 * d);
        var h = Math.sqrt((long) a.radius * a.radius - a2 * a2);

        var xm = a.x + a2 * dx / d;
        var ym = a.y + a2 * dy / d;

        return Stream.of(
                new CircleIntersect(xm + h * dy / d, ym - h * dx / d),
                new CircleIntersect(xm - h * dy / d, ym + h * dx / d));
    }
}

class Question {
    Area area;
    Circle[] circles;

    Question(Area area, Circle[] circles) {
        this.area = area;
        this.circles = circles;
    }
}

class Area {
    int width;
    int height;

    Area(int width, int height) {
        this.width = width;
        this.height = height;
    }
}

class Circle {
    int x;
    int y;
    int radius;

    Circle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}

interface IStep {
}

class RightLineStep implements IStep {
    double x;

    RightLineStep(double x) {
        this.x = x;
    }
}

class DownLineStep implements IStep {
    double y;

    DownLineStep(double y) {
        this.y = y;
    }
}

class ArcStep implements IStep {
    double x;
    double y;
    Circle circle;

    ArcStep(double x, double y, Circle circle) {
        this.x = x;
        this.y = y;
        this.circle = circle;
    }
}

class CornerEndStep implements IStep {
}

class LeftEndStep implements IStep {
    double y;

    LeftEndStep(double y) {
        this.y = y;
    }
}

class BottomEndStep implements IStep {
    double x;

    BottomEndStep(double x) {
        this.x = x;
    }
}

class ArcNext {
    double angle;
    IStep step;

    ArcNext(double angle, IStep step) {
        this.angle = angle;
        this.step = step;
    }
}

class ArcXIntersect {
    double x;
    double angle;

    ArcXIntersect(double x, double angle) {
        this.x = x;
        this.angle = angle;
    }
}

class ArcYIntersect {
    double y;
    double angle;

    ArcYIntersect(double y, double angle) {
        this.y = y;
        this.angle = angle;
    }
}

class CircleIntersect {
    double x;
    double y;

    CircleIntersect(double x, double y) {
        this.x = x;
        this.y = y;
    }
}