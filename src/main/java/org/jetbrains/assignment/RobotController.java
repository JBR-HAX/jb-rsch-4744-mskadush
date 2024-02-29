package org.jetbrains.assignment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

@RestController
public class RobotController {

    @PostMapping("/locations")
    ResponseEntity<List<Location>> locations(@RequestBody List<Move> moves) {
        // get start location
        Location currentLocation = new Location(0,0);
        List<Location> result = new ArrayList<>();
        result.add(currentLocation);
        // add direction & step
        for (Move move: moves) {
            Location newLocation = null;
            switch (move.direction()) {
                case EAST -> newLocation = new Location(currentLocation.x() + move.steps(), currentLocation.y());
                case WEST -> newLocation = new Location(currentLocation.x() - move.steps(), currentLocation.y());
                case NORTH -> newLocation = new Location(currentLocation.x(), currentLocation.y() + move.steps());
                case SOUTH -> newLocation = new Location(currentLocation.x(), currentLocation.y() - move.steps());
            }
            // append new location
            result.add(newLocation);
            currentLocation = newLocation;
        }
        return ResponseEntity.ok(result);
    }
    @PostMapping("/moves")
    ResponseEntity<List<Move>> moves(@RequestBody List<Location> locations) {
        // get start location
        List<Move> result = new ArrayList<>();
        for (int i = 0; i < locations.size() - 1; i++) {
            // diff between current location and next to get move
            Location startLocation = locations.get(i);
            Location endLocation = locations.get(i + 1);
            // assume robot will never move diagonally.
            // i.e. either x diff will be 0 and y diff will not be 0 or vice versa
            if (startLocation.y() < endLocation.y()) { // movedUp
                result.add(new Move(Direction.NORTH, abs(abs(endLocation.y()) - abs(startLocation.y()))));
            } else if (startLocation.y() > endLocation.y()) { // movedDown
                result.add(new Move(Direction.SOUTH, abs(abs(startLocation.y()) - abs(endLocation.y()))));
            } else if (startLocation.x() < endLocation.x()) { // movedRight
                result.add(new Move(Direction.EAST, abs(abs(endLocation.x()) - abs(startLocation.x()))));
            } else if (startLocation.x() > endLocation.x()) { // movedLeft
                result.add(new Move(Direction.WEST, abs(abs(startLocation.x()) - abs(endLocation.x()))));
            }
            // add move
        }
        return ResponseEntity.ok(result);
    }
}

enum Direction {
    EAST, WEST, SOUTH, NORTH;
}

record Location (int x, int y) {}

record Move(Direction direction, int steps) {}
