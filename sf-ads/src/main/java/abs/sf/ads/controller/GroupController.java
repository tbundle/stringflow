package abs.sf.ads.controller;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import abs.sf.ads.entity.Group.AccessMode;
import abs.sf.ads.entity.Group.GroupMember;
import abs.sf.ads.request.CreateGroupRequest;
import abs.sf.ads.service.GroupService;

@RestController
@RequestMapping("/group")
public class GroupController extends AbstractController<GroupService> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

	public GroupController(GroupService service) {
		super(service);
	}

	@PostMapping
	public ResponseEntity<?> createGroup(@NotNull @RequestBody CreateGroupRequest createGroupRequest) {
		LOGGER.info("Processing Create group request", createGroupRequest.toString());
		try {

			return service.createGroup(createGroupRequest);

		} catch (Exception e) {
			LOGGER.error("failed to process createGroup reuest", e);
			throw e;
		}
	}

	@PutMapping("/ugs/{groupJid}/{subject}")
	public ResponseEntity<?> updateGroupSubject(@NotNull @PathVariable("groupJid") String groupJid,
			@NotNull @PathVariable("subject") String subject) {
		LOGGER.info("Processing updateGroupSubject request for groupJid {}", groupJid);
		try {
			return service.updateGroupSubject(groupJid, subject);
		} catch (Exception e) {
			LOGGER.error("failed to update subject of groupJid {}", groupJid);
			throw e;
		}
	}

	@PutMapping("/ugam/{groupJid}/{accessMode}")
	public ResponseEntity<?> updateGroupAccessMode(@NotNull @PathVariable("groupJid") String groupJid,
			@NotNull @PathVariable("accessMode") AccessMode accessMode) {
		LOGGER.info("Processing updateGroupAccessMode request for groupJid {}", groupJid);
		try {
			return service.updateGroupAccessMode(groupJid, accessMode);
		} catch (Exception e) {
			LOGGER.error("failed to update accessMode of groupJid {}", groupJid);
			throw e;
		}
	}

	@PutMapping("/agm/{groupJid}")
	public ResponseEntity<?> addGroupMember(@NotNull @PathVariable("groupJid") String groupJid,
			@NotNull @RequestBody GroupMember member) {
		LOGGER.info("Processing addGroupMember request for groupJid {}", groupJid);
		try {
			return service.addGroupMember(groupJid, member);
		} catch (Exception e) {
			LOGGER.error("failed to add group member in groupJid {}", groupJid);
			throw e;
		}
	}

	@PutMapping("/rgm/{groupJid}/{memberJid}")
	public ResponseEntity<?> removeGroupMember(@PathVariable("groupJid") String groupJid,
			@PathVariable("memberJid") String memberJid) {
		LOGGER.info("Processing removeGroupMember request for groupJid {} and memberJid {}", groupJid, memberJid);
		try {
			return service.removeGroupMember(groupJid, memberJid);
		} catch (Exception e) {
			LOGGER.error("failed to remove group member in groupId {}", groupJid);
			throw e;
		}
	}

	@PutMapping("ugmd/{groupJid}")
	public ResponseEntity<?> updateGroupMemberDetail(@PathVariable("groupJid") String groupJid,
			@NotNull @RequestBody GroupMember member) {
		LOGGER.info("Processing updateGroupMemberDetail request for groupJid {} and member {}", groupJid,
				member.toString());
		try {
			return service.updateGroupMemberDetail(groupJid, member);
		} catch (Exception e) {
			LOGGER.error("failed to group member detail for groupId {}", groupJid);
			throw e;
		}
	}

	@DeleteMapping("{groupJid}")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupJid") String groupJid) {
		LOGGER.info("Processing deleteGroup request for groupJid {} ", groupJid);
		try {

			return service.deleteGroup(groupJid);

		} catch (Exception e) {
			LOGGER.error("failed to delete group for groupId {}", groupJid);
			throw e;
		}
	}
}
