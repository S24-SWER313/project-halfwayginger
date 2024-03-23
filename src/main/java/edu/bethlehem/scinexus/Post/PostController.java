package edu.bethlehem.scinexus.Post;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.hateoas.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {


  private final PostService postService;



  @GetMapping("/{postId}")
  public ResponseEntity<EntityModel<Post>> one(@PathVariable
                                               long postId) {
      return ResponseEntity.ok(postService.findPostById(postId));
  }
  @GetMapping()
  public CollectionModel<EntityModel<Post>> all() {
    List<EntityModel<Post>> posts = postService.findAllPosts();

    return CollectionModel.of(posts, linkTo(methodOn(PostController.class).all()).withSelfRel());
  }


  @PostMapping()
  public ResponseEntity<?> createNewPost( @Valid
                                          @RequestBody
                                          @NotNull
                                          PostRequestDTO newPostRequestDTO) {

    EntityModel<Post> entityModel = postService.createPost(newPostRequestDTO);
    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editPost( @Valid
                                     @RequestBody
                                     @NotNull
                                       PostRequestDTO newPostRequestDTO,
                                     @PathVariable
                                     @NotNull
                                     Long id) {

    EntityModel<Post> entityModel=postService.updatePost(newPostRequestDTO,id);

    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updatePostPartially(@PathVariable(value = "id")
                                               Long postId,

                                               @RequestBody
                                               @NotNull
                                               PostRequestPatchDTO newPostRequestDTO) {

        EntityModel<Post> entityModel=postService.updatePostPartially(postId, newPostRequestDTO);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<?> deletePost(@PathVariable Long id) {
    postService.deletePost(id);

    return ResponseEntity.noContent().build();
  }



}
