package okestro.internproject.domain.game.entity.memory;

import lombok.Builder;
import lombok.Getter;
import okestro.internproject.domain.game.enums.GomokuBoardState;
import okestro.internproject.domain.game.enums.GomokuTurn;
import okestro.internproject.domain.game.enums.PutResult;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class GomokuInstance implements GameInstance {

    static private final int BOARD_SIZE = 15;
    static private final int[][] startDirections = {
            {0, -1},
            {-1, -1},
            {-1, 0},
            {1, -1}
    };
    static private final int[][] moveDirections = {
            {0, 1},
            {1, 1},
            {1, 0},
            {-1, 1}
    };


    private final SimpleUser player1;
    private final SimpleUser player2;
    private int player1Score = 0;
    private int player2Score = 0;
    private final GomokuBoardState[][] board;
    private final LocalDateTime startTime;
    private LocalDateTime finishTime;
    private GomokuTurn currTurn = null;
    private final UUID blackUserId;
    private final UUID whiteUserId;


    @Builder
    public GomokuInstance(SimpleUser player1, SimpleUser player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new GomokuBoardState[BOARD_SIZE + 1][BOARD_SIZE + 1];
        for (int row = 1; row <= BOARD_SIZE; row++) {
            for (int col = 1; col <= BOARD_SIZE; col++)
                putStone(row, col, GomokuBoardState.EMPTY);
        }
        blackUserId = (Math.random() < 0.5) ?
                player1.getId() : player2.getId();
        whiteUserId = (blackUserId == player1.getId()) ?
                player2.getId() : player1.getId();
        startTime = LocalDateTime.now();
    }

    public PutResult putStone(int row, int col) {
        PutResult putResult = getPutStoneResult(row, col);
        if (putResult.equals(PutResult.SUCCESS)) {
            putStone(row, col, getCurrentTurnStone());
            return isFinished(row, col) ? PutResult.FINISH : PutResult.SUCCESS;
        }
        return putResult;
    }

    private void putStone(int row, int col, GomokuBoardState stone) {
        board[row][col] = stone;
    }

    @Override
    public void startGame() {
    }

    @Override
    public void finishGame() {
        if (currTurn.equals(GomokuTurn.BLACK)) {
            player1Score += player1.getId().equals(blackUserId) ? 1 : 0;
            player2Score += player2.getId().equals(blackUserId) ? 1 : 0;
        } else {
            player1Score += player1.getId().equals(whiteUserId) ? 1 : 0;
            player2Score += player2.getId().equals(whiteUserId) ? 1 : 0;
        }
        finishTime = LocalDateTime.now();
    }

    @Override
    public UUID changeTurnId() {
        this.currTurn = (this.currTurn == GomokuTurn.BLACK) ?
                GomokuTurn.WHITE : GomokuTurn.BLACK;
        return this.currTurn.equals(GomokuTurn.BLACK) ?
                blackUserId : whiteUserId;
    }

    @Override
    public UUID getWinnerId() {
        return player1Score > player2Score ?
                player1.getId() : player2.getId();
    }

    @Override
    public void leaveUserOnGame(UUID userId) {
        player1Score = userId.equals(player1.getId()) ? -1 : player1Score;
        player2Score = userId.equals(player2.getId()) ? -1 : player2Score;
    }

    public PutResult getPutStoneResult(int row, int col) {
        if (!(isInRange(row, col) && board[row][col] == GomokuBoardState.EMPTY)) {
            return PutResult.FAIL;
        }

        putStone(row, col, getCurrentTurnStone());
        PutResult putResult = getCurrentTurnStone().equals(GomokuBoardState.BLACK) ?
                checkForbidden(row, col) : PutResult.SUCCESS;
        putStone(row, col, GomokuBoardState.EMPTY);
        return putResult;
    }


    public PutResult checkForbidden(int row, int col) {
        if (checkOverFive(row, col)) {
            return PutResult.OVER_FIVE;
        } else if (checkDoubleFour(row, col)) {
            return PutResult.DOUBLE_FOUR;
        } else if (checkDoubleThree(row, col)) {
            return PutResult.DOUBLE_THREE;
        }
        return PutResult.SUCCESS;
    }

    private boolean checkOverFive(int row, int col) {
        for (int[] moveDirection : moveDirections) {
            int count = 0;
            for (int k = 1; k < 5; ++k) {
                int movedRow = row + k * moveDirection[0];
                int movedCol = col + k * moveDirection[1];

                if (!isValidStone(movedRow, movedCol, GomokuBoardState.BLACK)) {
                    break;
                }
                count++;
            }
            for (int k = 1; k < 5; ++k) {
                int movedRow = row - k * moveDirection[0];
                int movedCol = col - k * moveDirection[1];

                if (!isValidStone(movedRow, movedCol, GomokuBoardState.BLACK)) {
                    break;
                }
                count++;
            }
            if (count >= 5) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidStone(int row, int col, GomokuBoardState stone) {
        return isInRange(row, col) && board[row][col] == stone;
    }

    private boolean checkDoubleFour(int row, int col) {
        int count = 0;
        int checkStoneCount = 5;
        for (int d = 0; d < startDirections.length; ++d) {
            count += checkDoubleOneDirection(row, col, d, checkStoneCount);
        }
        return count >= 2;
    }

    private boolean checkDoubleThree(int row, int col) {
        int count = 0;
        int checkStoneCount = 4;
        for (int d = 0; d < startDirections.length; ++d) {
            count += checkDoubleOneDirection(row, col, d, checkStoneCount);
        }
        return count >= 2;
    }

    private int checkDoubleOneDirection(int row, int col, int d, int checkStoneCount) {
        int count = 0;
        for (int k = 0; k < checkStoneCount; ++k) {
            int startRow = row + k * startDirections[d][0];
            int startCol = col + k * startDirections[d][1];

            if (!isValidStone(startRow, startCol, GomokuBoardState.BLACK)) {
                continue;
            }

            int stoneCount = 0;
            int canEmpty = 1;
            for (int m = 0; m < 5; ++m) {
                int movedRow = startRow + m * moveDirections[d][0];
                int movedCol = startCol + m * moveDirections[d][1];

                if (!isValidCondition(movedRow, movedCol, canEmpty)) {
                    break;
                }

                canEmpty = (board[movedRow][movedCol] == GomokuBoardState.EMPTY) ?
                        canEmpty - 1 : canEmpty;
                stoneCount = (board[movedRow][movedCol] == GomokuBoardState.EMPTY) ?
                        stoneCount : stoneCount + 1;

                if (checkStoneCount == 4) {
                    if (stoneCount == 3 && isValidThreeStones(startRow, startCol, canEmpty, d)) {
                        count++;
                        break;
                    }
                } else {
                    if (stoneCount == 4 && isValidFourStones(startRow, startCol, canEmpty, d)) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    private boolean isValidFourStones(int startRow, int startCol, int canEmpty, int d) {
        int prevRow = startRow + startDirections[d][0];
        int prevCol = startCol + startDirections[d][1];
        int lastNextRow = startRow + moveDirections[d][0] * 4;
        int lastNextCol = startCol + moveDirections[d][1] * 4;

        if (canEmpty == 1) {
            if (isBlocked(prevRow, prevCol, GomokuBoardState.WHITE) &&
                    isBlocked(lastNextRow, lastNextCol, GomokuBoardState.WHITE)) {
                return false;
            }
        } else {
            lastNextRow += moveDirections[d][0];
            lastNextCol += moveDirections[d][1];
        }
        return !(isValidStone(prevRow, prevCol, GomokuBoardState.BLACK) ||
                isValidStone(lastNextRow, lastNextCol, GomokuBoardState.BLACK));
    }

    private boolean isBlocked(int row, int col, GomokuBoardState stone) {
        return (!isInRange(row, col) || board[row][col] == stone);
    }

    private boolean isValidThreeStones(int startRow, int startCol, int canEmpty, int d) {
        int prevRow = startRow + startDirections[d][0];
        int prevCol = startCol + startDirections[d][1];

        int beforePrevRow = startRow + startDirections[d][0] * 2;
        int beforePrevCol = startCol + startDirections[d][1] * 2;
        int afterNextRow = startRow + moveDirections[d][0] * 4;
        int afterNextCol = startCol + moveDirections[d][1] * 4;

        if (isBlocked(prevRow, prevCol, GomokuBoardState.WHITE)) {
            return false;
        }

        if (canEmpty == 1) {
            int lastRow = startRow + moveDirections[d][0] * 3;
            int lastCol = startCol + moveDirections[d][1] * 3;

            if (isBlocked(lastRow, lastCol, GomokuBoardState.WHITE)) {
                return false;
            }
            if (isValidStone(prevRow, prevCol, GomokuBoardState.BLACK) ||
                    isValidStone(lastRow, lastCol, GomokuBoardState.BLACK)) {
                return false;
            }

            return !(isValidStone(beforePrevRow, beforePrevCol, GomokuBoardState.WHITE) &&
                    isValidStone(afterNextRow, afterNextCol, GomokuBoardState.WHITE));
        } else {
            if (isValidStone(afterNextRow, afterNextCol, GomokuBoardState.WHITE)) {
                return false;
            }

            return !(isValidStone(prevRow, prevCol, GomokuBoardState.BLACK) ||
                    isValidStone(afterNextRow, afterNextCol, GomokuBoardState.BLACK));
        }
    }

    private boolean isValidCondition(int currRow, int currCol, int canEmpty) {
        return (
                isInRange(currRow, currCol) &&
                        ((board[currRow][currCol] == GomokuBoardState.BLACK) ||
                                (board[currRow][currCol] == GomokuBoardState.EMPTY &&
                                        canEmpty > 0)))
                ;
    }

    public boolean isFinished(int row, int col) {
        for (int[] moveDirection : moveDirections) {
            int count = 1;
            for (int k = 1; k < 5; ++k) {
                int movedRow = row + k * moveDirection[0];
                int movedCol = col + k * moveDirection[1];

                if (!isValidStone(movedRow, movedCol, getCurrentTurnStone())) {
                    break;
                }
                count++;
            }
            for (int k = 1; k < 5; ++k) {
                int movedRow = row - k * moveDirection[0];
                int movedCol = col - k * moveDirection[1];

                if (!isValidStone(movedRow, movedCol, getCurrentTurnStone())) {
                    break;
                }
                count++;
            }
            if (count >= 5) {
                return true;
            }
        }
        return false;
    }

    public GomokuBoardState getCurrentTurnStone() {
        return currTurn.equals(GomokuTurn.BLACK) ? GomokuBoardState.BLACK : GomokuBoardState.WHITE;
    }

    private boolean isInRange(int row, int col) {
        return row >= 1 && row <= BOARD_SIZE && col >= 1 && col <= BOARD_SIZE;
    }
}
