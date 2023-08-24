package okestro.internproject.domain.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserGameRecordPageDto {
    private List<UserGameRecordDto> userGameRecords;
    private long totalElements;
}
