package in28mins.restws.rest.users;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import in28mins.restws.exceptions.PostsNotFoundException4User;
import in28mins.restws.exceptions.UserNotFoundException;
import in28mins.restws.model.posts.Post;
import in28mins.restws.model.users.User;
import in28mins.restws.repos.posts.PostsRepository;
import in28mins.restws.repos.users.UsersRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersRestController
{

    private static final String relAllUsers = "all-Users";
    private static final String relUserDetails = "userDetails";
    private static final String relUserPosts = "posts";
    private final UsersRepository repoUsers;
    private final PostsRepository repoPosts;

    @GetMapping("/")
    public List<User> getUsers()
    {
        return repoUsers.findAll();
    }

    @GetMapping("/{id}")
    public EntityModel<User> getUserById(@PathVariable int id)
    {
        EntityModel<User> eM = null;
        User userFound = null;
        Optional<User> userO = null;

        if (id >= 1)
        {
            userO = repoUsers.findById(id);
            if (userO.isPresent())
            {
                userFound = userO.get();
                if (userFound != null)
                {
                    eM = EntityModel.of(userFound);
                    WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).getUsers());
                    WebMvcLinkBuilder link2Posts = linkTo(methodOn(this.getClass()).getPosts4UserById(id));
                    eM.add(link.withRel(relAllUsers));
                    eM.add(link2Posts.withRel(relUserPosts));
                }

            }
            else
            {
                throw new UserNotFoundException("User not found for id : " + id);
            }
        }
        return eM;
    }

    @GetMapping("/{userId}/posts/{id}")
    public EntityModel<Post> getPost4UserbyPostId(@PathVariable int userId, @PathVariable int id)
    {
        EntityModel<Post> eM = null;
        if (userId > 0)
        {
            // Get the User
            Optional<User> userO = repoUsers.findById(userId);
            if (userO.isPresent())
            {
                // Get Posts for User
                if (!CollectionUtils.isEmpty(userO.get().getPosts()))
                {
                    Optional<Post> postO = userO.get().getPosts().stream().filter(p -> p.getId() == id).findFirst();
                    if (postO.isPresent())
                    {
                        eM = EntityModel.of(postO.get());
                        WebMvcLinkBuilder link2Posts = linkTo(methodOn(this.getClass()).getPosts4UserById(userId));
                        eM.add(link2Posts.withRel(relUserPosts));

                    }
                    else
                    {
                        throw new PostsNotFoundException4User("No Post with Id : " + id + "found for User : " + userId);
                    }

                }
                else
                {
                    throw new PostsNotFoundException4User("No Posts exists for User : " + userId);
                }
            }
            else
            {
                throw new UserNotFoundException("User not found for id : " + userId);
            }
        }

        return eM;
    }

    @GetMapping("/{id}/posts")
    public CollectionModel<EntityModel<Post>> getPosts4UserById(@PathVariable int id)
    {
        CollectionModel<EntityModel<Post>> cM = null;
        List<EntityModel<Post>> posts = null;
        User userFound = null;
        Optional<User> userO = null;

        if (id >= 1)
        {
            userO = repoUsers.findById(id);
            if (userO.isPresent())
            {
                userFound = userO.get();
                if (userFound != null)
                {
                    if (!CollectionUtils.isEmpty(userFound.getPosts()))
                    {
                        posts = new ArrayList<EntityModel<Post>>();
                        for (Post post : userFound.getPosts())
                        {
                            if (post != null)
                            {
                                posts.add(EntityModel.of(post));
                            }

                        }
                    }
                    else
                    {
                        throw new PostsNotFoundException4User("Post(s) not found for User id : " + id);
                    }

                    cM = CollectionModel.of(posts);
                    WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).getUsers());
                    cM.add(link.withRel(relAllUsers));
                    WebMvcLinkBuilder linkUserDetails = linkTo(methodOn(this.getClass()).getUserById(id));
                    cM.add(linkUserDetails.withRel(relUserDetails));
                }

            }
            else
            {
                throw new UserNotFoundException("User not found for id : " + id);
            }
        }
        return cM;
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id)
    {
        User userFound = null;
        Optional<User> userO = null;

        if (id >= 1)
        {
            userO = repoUsers.findById(id);
            if (userO.isPresent())
            {
                userFound = userO.get();
                repoUsers.delete(userFound);
            }
            else
            {
                throw new UserNotFoundException("User not found for id : " + id);
            }
        }

    }

    @PostMapping("/")
    public ResponseEntity<User> postMethodName(@Valid @RequestBody @NonNull User newUser)
    {

        // SAve the Entity
        User createdUser = repoUsers.save(newUser);

        // Create a new HttpHeaders object
        HttpHeaders headers = new HttpHeaders();

        // Set Header location using current Path
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        headers.setLocation(location);

        return new ResponseEntity<>(createdUser, headers, HttpStatus.CREATED);

    }

    @PostMapping("/{userId}/posts")
    public ResponseEntity<Post> createPost(@PathVariable int userId, @Valid @RequestBody @NonNull Post newPost)
    {
        // Get User by User Id
        Post savedPost = null;
        Optional<User> userO = repoUsers.findById(userId);
        if (userO.isPresent() && repoPosts != null)
        {
            // Create the Post
            newPost.setUser(userO.get());
            savedPost = repoPosts.save(newPost);
        }
        else
        {
            throw new UserNotFoundException("User not found for ID : " + userId);
        }

        // Create a new HttpHeaders object
        HttpHeaders headers = new HttpHeaders();

        // Set Header location using current Path
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedPost.getId())
                .toUri();
        headers.setLocation(location);

        return new ResponseEntity<Post>(savedPost, headers, HttpStatus.CREATED);
    }

}
