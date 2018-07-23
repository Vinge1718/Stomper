<<<<<<< HEAD
package io.github.vinge1718.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import io.github.vinge1718.MyProgrammingMario;
import io.github.vinge1718.Screens.PlayScreen;

public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private TextureRegion shell;
    private float deadRotationDegrees;


    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),0,0,16,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),16,0,16,24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"),64,0,16,24);
        walkAnimation = new Animation<TextureRegion>(0.2f,frames );
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;

        setBounds(getX(), getY(), 16/ MyProgrammingMario.PPM, 24/MyProgrammingMario.PPM);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() , getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/ MyProgrammingMario.PPM);
        fdef.filter.categoryBits = MyProgrammingMario.ENEMY_BIT;
        fdef.filter.maskBits = MyProgrammingMario.GROUND_BIT |
                MyProgrammingMario.COIN_BIT |
                MyProgrammingMario.BRICK_BIT |
                MyProgrammingMario.ENEMY_BIT |
                MyProgrammingMario.OBJECT_BIT |
                MyProgrammingMario.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-4, 8).scl(1/MyProgrammingMario.PPM);
        vertice[1] = new Vector2(4, 8).scl(1/MyProgrammingMario.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1/MyProgrammingMario.PPM);
        vertice[3] = new Vector2(3, 3).scl(1/MyProgrammingMario.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.8f;
        fdef.filter.categoryBits = MyProgrammingMario.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void onEnemyHit(Enemy enemy){
//        if(enemy instanceof Turtle){
//            if(((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
//                killed();
//            } else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING)
//                return;
//            else
//                reverseVelocity(true, false);
//        } else if (currentState != State.MOVING_SHELL )
            reverseVelocity(true, false);
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;
        switch (currentState){
            case MOVING_SHELL:
            case STANDING_SHELL:
                region = shell;
                break;
            case WALKING:
                default:
                    region = walkAnimation.getKeyFrame(stateTime, true);
                    break;
        }

        if (velocity.x > 0 && region.isFlipX() == false){
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX() == true){
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState= currentState;
        return region;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x =1;
            System.out.print("Wake Up Shell!");
        }

        setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - 8/MyProgrammingMario.PPM);
//        if(currentState == State.DEAD){
//            deadRotationDegrees += 3;
//            rotate(deadRotationDegrees);
//            if(stateTime > 5 && !destroyed){
//                world.destroyBody(b2body);
//                destroyed = true;
//            }
//        }
//        else
            b2body.setLinearVelocity(velocity);
    }

//    public void kick(int speed){
//        velocity.x = speed;
//        currentState = State.MOVING_SHELL;
//    }

    public void kick(int direction){
        velocity.x = direction;
        currentState = State.MOVING_SHELL;
    }

    @Override
    public void hitOnHead(Mario mario) {
//        if(currentState != State.STANDING_SHELL){
//            currentState = State.STANDING_SHELL;
//            velocity.x = 0;
//        } else{
//            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
//        }
//    }
//
//    public void draw(Batch batch) {
//        if(!destroyed)
//            super.draw(batch);
        if(currentState == State.STANDING_SHELL){
            if(mario.b2body.getPosition().x > b2body.getPosition().x)
                velocity.x = -2;
            else
                velocity.x =2;
            currentState = State.MOVING_SHELL;
            System.out.print("Set to moving shell");
        }
        else{
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }
    }

    public State getCurrentState(){
        return currentState;
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MyProgrammingMario.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }
}
=======
package io.github.vinge1718.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import io.github.vinge1718.MyProgrammingMario;
import io.github.vinge1718.Screens.PlayScreen;

public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private TextureRegion shell;
    private float deadRotationDegrees;


    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),0,0,16,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),16,0,16,24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"),64,0,16,24);
        walkAnimation = new Animation<TextureRegion>(0.2f,frames );
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;

        setBounds(getX(), getY(), 16/ MyProgrammingMario.PPM, 24/MyProgrammingMario.PPM);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() , getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6/ MyProgrammingMario.PPM);
        fdef.filter.categoryBits = MyProgrammingMario.ENEMY_BIT;
        fdef.filter.maskBits = MyProgrammingMario.GROUND_BIT |
                MyProgrammingMario.COIN_BIT |
                MyProgrammingMario.BRICK_BIT |
                MyProgrammingMario.ENEMY_BIT |
                MyProgrammingMario.OBJECT_BIT |
                MyProgrammingMario.MARIO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-4, 8).scl(1/MyProgrammingMario.PPM);
        vertice[1] = new Vector2(4, 8).scl(1/MyProgrammingMario.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1/MyProgrammingMario.PPM);
        vertice[3] = new Vector2(3, 3).scl(1/MyProgrammingMario.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.8f;
        fdef.filter.categoryBits = MyProgrammingMario.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void onEnemyHit(Enemy enemy){
//        if(enemy instanceof Turtle){
//            if(((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
//                killed();
//            } else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING)
//                return;
//            else
//                reverseVelocity(true, false);
//        } else if (currentState != State.MOVING_SHELL )
            reverseVelocity(true, false);
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;
        switch (currentState){
            case MOVING_SHELL:
            case STANDING_SHELL:
                region = shell;
                break;
            case WALKING:
                default:
                    region = walkAnimation.getKeyFrame(stateTime, true);
                    break;
        }

        if (velocity.x > 0 && region.isFlipX() == false){
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX() == true){
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState= currentState;
        return region;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x =1;
            System.out.print("Wake Up Shell!");
        }

        setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - 8/MyProgrammingMario.PPM);
//        if(currentState == State.DEAD){
//            deadRotationDegrees += 3;
//            rotate(deadRotationDegrees);
//            if(stateTime > 5 && !destroyed){
//                world.destroyBody(b2body);
//                destroyed = true;
//            }
//        }
//        else
            b2body.setLinearVelocity(velocity);
    }

//    public void kick(int speed){
//        velocity.x = speed;
//        currentState = State.MOVING_SHELL;
//    }

    public void kick(int direction){
        velocity.x = direction;
        currentState = State.MOVING_SHELL;
    }

    @Override
    public void hitOnHead(Mario mario) {
//        if(currentState != State.STANDING_SHELL){
//            currentState = State.STANDING_SHELL;
//            velocity.x = 0;
//        } else{
//            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
//        }
//    }
//
//    public void draw(Batch batch) {
//        if(!destroyed)
//            super.draw(batch);
        if(currentState == State.STANDING_SHELL){
            if(mario.b2body.getPosition().x > b2body.getPosition().x)
                velocity.x = -2;
            else
                velocity.x =2;
            currentState = State.MOVING_SHELL;
            System.out.print("Set to moving shell");
        }
        else{
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }
    }

    public State getCurrentState(){
        return currentState;
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MyProgrammingMario.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }
}
>>>>>>> 9e27586d1baa98fc5feea72bcb6d08330f2204cc