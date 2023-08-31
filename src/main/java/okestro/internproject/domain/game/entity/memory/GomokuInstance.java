package okestro.internproject.domain.game.entity.memory;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.enums.GomokuBoardState;
import okestro.internproject.domain.game.enums.GomokuTurn;
import okestro.internproject.domain.game.enums.PutResult;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
public class GomokuInstance implements GameInstance {

    static private final int BOARD_SIZE = 15;
    static private final int[][] startMoved = {
            {0, -1},
            {-1, -1},
            {-1, 0},
            {1, -1}
    };
    static private final int[][] currMoved = {
            {0, 1},
            {1, 1},
            {1, 0},
            {-1, 1}
    };


    private final SimpleUser player1;
    private final SimpleUser player2;
    private int player1Score = 1;
    private int player2Score = 1;
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
                board[row][col] = GomokuBoardState.EMPTY;
        }
        blackUserId = Math.random() < 0.5 ? player1.getId() : player2.getId();
        whiteUserId = blackUserId == player1.getId() ? player2.getId() : player1.getId();
        startTime = LocalDateTime.now();
    }

    public PutResult putStone(int row, int col) {
        switch (isCanPutStone(row, col)) {
            case DOUBLE_THREE:
                return PutResult.DOUBLE_THREE;
            case DOUBLE_FOUR:
                return PutResult.DOUBLE_FOUR;
            case OVER_FIVE:
                return PutResult.OVER_FIVE;
            case FAIL:
                return PutResult.FAIL;
        }

        board[row][col] = getCurrentTurnStone();

        if (isFinished()) {
            finishGame();
            return PutResult.FINISH;
        }

        return PutResult.SUCCESS;
    }

    @Override
    public void startGame() {
    }

    @Override
    public void finishGame() {
        finishTime = LocalDateTime.now();
    }

    @Override
    public UUID changeTurnId() {
        if (this.currTurn == null) {
            this.currTurn = GomokuTurn.BLACK;
            return blackUserId;
        } else if (this.currTurn.equals(GomokuTurn.BLACK)) {
            this.currTurn = GomokuTurn.WHITE;
            return whiteUserId;
        } else {
            this.currTurn = GomokuTurn.BLACK;
            return blackUserId;
        }
    }

    @Override
    public UUID getWinnerId() {
        return player1Score > player2Score ? player1.getId() : player2.getId();
    }

    @Override
    public void leaveUserOnGame(UUID userId) {
        if (userId.equals(player1.getId())) {
            player1Score = -1;
        } else {
            player2Score = -1;
        }
    }

    public PutResult isCanPutStone(int row, int col) {
        if (!(isInRange(row, col) && board[row][col] == GomokuBoardState.EMPTY)) {
            return PutResult.FAIL;
        }

        board[row][col] = getCurrentTurnStone();

        if (getCurrentTurnStone().equals(GomokuBoardState.BLACK)) {
            switch (checkForbidden(row, col)) {
                case DOUBLE_THREE: {
                    board[row][col] = GomokuBoardState.EMPTY;
                    return PutResult.DOUBLE_THREE;
                }
                case DOUBLE_FOUR: {
                    board[row][col] = GomokuBoardState.EMPTY;
                    return PutResult.DOUBLE_FOUR;
                }
                case OVER_FIVE: {
                    board[row][col] = GomokuBoardState.EMPTY;
                    return PutResult.OVER_FIVE;
                }
            }
        }
        board[row][col] = GomokuBoardState.EMPTY;
        return PutResult.SUCCESS;
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
        for (int d = 0; d < currMoved.length; ++d) {
            int count = 0;
            for (int k = 1; k < 5; ++k) {
                int currRow = row + k * currMoved[d][0];
                int currCol = col + k * currMoved[d][1];

                if (!isInRange(currRow, currCol) ||
                        board[currRow][currCol] != getCurrentTurnStone()) {
                    break;
                }
                count++;
            }
            for (int k = 1; k < 5; ++k) {
                int currRow = row - k * currMoved[d][0];
                int currCol = col - k * currMoved[d][1];

                if (!isInRange(currRow, currCol) ||
                        board[currRow][currCol] != getCurrentTurnStone()) {
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

    private boolean checkDoubleFour(int row, int col) {
        int count = 0;
        int checkStoneCount = 5;
        for (int d = 0; d < startMoved.length; ++d) {
            count += checkDoubleOneDirection(row, col, d, checkStoneCount);
        }
        return count >= 2;
    }

    private boolean checkDoubleThree(int row, int col) {
        int count = 0;
        int checkStoneCount = 4;
        for (int d = 0; d < startMoved.length; ++d) {
            count += checkDoubleOneDirection(row, col, d, checkStoneCount);
        }
        return count >= 2;
    }

    private int checkDoubleOneDirection(int row, int col, int d, int checkStoneCount) {
        int count = 0;

        for (int k = 0; k < checkStoneCount; ++k) {
            int startRow = row + k * startMoved[d][0];
            int startCol = col + k * startMoved[d][1];

            if (!isInRange(startRow, startCol) ||
                    board[startRow][startCol] != GomokuBoardState.BLACK) {
                continue;
            }

            int stoneCount = 0;
            int canEmpty = 1;
            for (int m = 0; m < 5; ++m) {
                int currRow = startRow + m * currMoved[d][0];
                int currCol = startCol + m * currMoved[d][1];

                if (!isValidCondition(currRow, currCol, canEmpty)) {
                    break;
                }

                if (board[currRow][currCol] == GomokuBoardState.EMPTY) {
                    canEmpty--;
                } else {
                    stoneCount++;
                    if (checkStoneCount == 4) {
                        if (stoneCount == 3 && isValidThreeStones(startRow, startCol, canEmpty, d)) {
                            count++;
                            break;
                        }
                    } else {
                        if (stoneCount == 4 && isValidFourStones(startRow, startCol, canEmpty, d))
                            count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    private boolean isValidFourStones(int startRow, int startCol, int canEmpty, int d) {
        int currRow = startRow + startMoved[d][0];
        int currCol = startCol + startMoved[d][1];
        int lastRow = startRow + currMoved[d][0] * 4;
        int lastCol = startCol + currMoved[d][1] * 4;

        if (canEmpty == 1) {
            if (!isInRange(currRow, currCol) ||
                    board[currRow][currCol] == GomokuBoardState.WHITE) {
                if (!isInRange(lastRow, lastCol) ||
                        board[lastRow][lastCol] == GomokuBoardState.WHITE) {
                    return false;
                }
            }

            if ((isInRange(currRow, currCol) &&
                    board[currRow][currCol] == GomokuBoardState.BLACK) ||
                    (isInRange(lastRow, lastCol) &&
                            board[lastRow][lastCol] == GomokuBoardState.BLACK)) {
                return false;
            }
        } else {
            int nextRow = startRow + currMoved[d][0] * 5;
            int nextCol = startCol + currMoved[d][1] * 5;

            if ((isInRange(currRow, currCol) && board[currRow][currCol] == GomokuBoardState.BLACK) ||
                    isInRange(nextRow, nextCol) && board[nextRow][nextCol] == GomokuBoardState.BLACK) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidThreeStones(int startRow, int startCol, int canEmpty, int d) {
        int currRow = startRow + startMoved[d][0];
        int currCol = startCol + startMoved[d][1];


        int prevCurrRow = startRow + currMoved[d][0] * 2;
        int prevCurrCol = startCol + currMoved[d][1] * 2;
        int nextLastRow = startRow + currMoved[d][0] * 4;
        int nextLastCol = startCol + currMoved[d][1] * 4;


        if (canEmpty == 1) {
            if (!isInRange(currRow, currCol) ||
                    board[currRow][currCol] == GomokuBoardState.WHITE) {
                return false;
            }

            int lastRow = startRow + currMoved[d][0] * 3;
            int lastCol = startCol + currMoved[d][1] * 3;

            if (!isInRange(lastRow, lastCol) || board[lastRow][lastCol] == GomokuBoardState.WHITE) {
                return false;
            }

            if ((isInRange(currRow, currCol) &&
                    board[currRow][currCol] == GomokuBoardState.BLACK) ||
                    (isInRange(lastRow, lastCol) &&
                            board[currRow][currCol] == GomokuBoardState.BLACK)) {
                return false;
            }

            if (isInRange(prevCurrRow, prevCurrCol) &&
                    board[prevCurrRow][prevCurrCol] == GomokuBoardState.WHITE &&
                    isInRange(nextLastRow, nextLastCol) &&
                    board[nextLastRow][nextLastCol] == GomokuBoardState.WHITE) {
                return false;
            }
        } else {
            if (!isInRange(currRow, currCol) ||
                    board[currRow][currCol] == GomokuBoardState.WHITE) {
                return false;
            }

            if (!isInRange(nextLastRow, nextLastCol)
                    || board[nextLastRow][nextLastCol] == GomokuBoardState.WHITE) {
                return false;
            }

            if (
                    (isInRange(currRow, currCol) &&
                            board[currRow][currCol] == GomokuBoardState.BLACK) ||
                            (isInRange(nextLastRow, nextLastCol) &&
                                    board[nextLastRow][nextLastCol] == GomokuBoardState.BLACK)
            ) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidCondition(int currRow, int currCol, int canEmpty) {
        return (
                isInRange(currRow, currCol) &&
                        ((board[currRow][currCol] == GomokuBoardState.BLACK) ||
                                (board[currRow][currCol] == GomokuBoardState.EMPTY &&
                                        canEmpty > 0)))
                ;
    }

    public boolean isFinished() {
        for (int row = 1; row <= BOARD_SIZE; ++row) {
            for (int col = 1; col <= BOARD_SIZE; ++col) {
                if (checkHorizontal(row, col)
                        || checkVertical(row, col)
                        || checkDiagonal(row, col)) {
                    if (currTurn.equals(GomokuTurn.BLACK))
                        if (player1.getId().equals(blackUserId)) {
                            player1Score++;
                        } else {
                            player2Score++;
                        }
                    else {
                        if (player1.getId().equals(whiteUserId)) {
                            player1Score++;
                        } else {
                            player2Score++;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private boolean checkHorizontal(int row, int col) {
        for (int i = 0; i < 5; ++i) {
            if (!isInRange(row, col + i) || board[row][col + i] != getCurrentTurnStone()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkVertical(int row, int col) {
        for (int i = 0; i < 5; ++i) {
            if (!isInRange(row + i, col) || board[row + i][col] != getCurrentTurnStone()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonal(int row, int col) {
        return (
                checkDiagonalFromRightTop(row, col) ||
                        checkDiagonalFromLeftTop(row, col)
        );
    }

    private boolean checkDiagonalFromRightTop(int row, int col) {
        for (int i = 0; i < 5; ++i) {
            if (!isInRange(row + i, col - i) || board[row + i][col - i] != getCurrentTurnStone()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiagonalFromLeftTop(int row, int col) {
        for (int i = 0; i < 5; ++i) {
            if (!isInRange(row + i, col + i) || board[row + i][col + i] != getCurrentTurnStone()) {
                return false;
            }
        }
        return true;
    }


    public GomokuBoardState getCurrentTurnStone() {
        return currTurn.equals(GomokuTurn.BLACK) ? GomokuBoardState.BLACK : GomokuBoardState.WHITE;
    }

    private boolean isInRange(int row, int col) {
        return row >= 1 && row <= BOARD_SIZE && col >= 1 && col <= BOARD_SIZE;
    }
}
